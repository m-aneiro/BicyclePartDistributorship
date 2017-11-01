package BicyclePartDistributorshipAPI.Controllers;

import java.io.IOException;
import java.util.Base64;

import BicyclePartDistributorshipAPI.Models.User;
import BicyclePartDistributorshipAPI.Models.User.UserType;
import Database.Database;
import Tools.Security;
import BicyclePartDistributorshipAPI.DataLayer.DatabaseConnection;

public class UserController {
	private Database<User> users;

	public UserController() throws IOException {
		this.users = (new DatabaseConnection().getUsers());
	}

	/**
	 * Checks password against password stored for user in the database
	 * @param username Username of user to check
	 * @param password Password to check against
	 * @return User model of authenticated user if authentication sucessful, otherwise null
	 * @throws IOException Error in reading from database
	 */
	public User authenticateLogin(String username, String password) throws IOException {
		User user = users.getValue(username);
		if(user == null) {
			return null;
		}

		byte[] salt = Base64.getDecoder().decode(user.getPasswordSalt());
		String attemptedPasswordHash = Base64.getEncoder().encodeToString(Security.hashPassword(password.toCharArray(), salt));

		if(attemptedPasswordHash.equals(user.getPasswordHash())) {
			return user;
		}
		else return null;
	}

	public void registerUser(String username, String password, String email, UserType userType) throws IOException {
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setUserType(userType);

		//Generate a salt and hash the password
		byte[] salt = Security.generateSalt();
		byte[] hash = Security.hashPassword(password.toCharArray(), salt);

		//Encode results in base64
		String base64EncodedSalt = Base64.getEncoder().encodeToString(salt);
		String base64EncodedHash = Base64.getEncoder().encodeToString(hash);

		//Store Base64 encoded results
		user.setPasswordHash(base64EncodedHash);
		user.setPasswordSalt(base64EncodedSalt);

		users.addValue(user);
	}
}