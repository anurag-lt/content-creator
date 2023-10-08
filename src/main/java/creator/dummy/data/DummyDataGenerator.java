package creator.dummy.data;

import creator.utils.DBUtils;

public class DummyDataGenerator {
	public static void main(String[] args) {
		insertAmentites();
		insertUser();
		insertSpaces();
		insertBookings();
		insertReview();
		insertPayment();
		insertspaceamenities();
		insertspaceimages();	
	}

	private static DBUtils dbUtils = DBUtils.getInstance();

	private static void insertspaceimages(int spaceId, String imageUrl) {
		String query = String.format("INSERT INTO space_images(space_id, image_url) VALUES(%d, '%s')", spaceId,
				imageUrl);
		dbUtils.insertIntoDB(query);
	}

	private static void insertspaceamenities(int spaceId, int amenityId) {
		String query = String.format("INSERT INTO space_amenities(space_id, amenity_id) VALUES(%d, %d)", spaceId,
				amenityId);
		dbUtils.insertIntoDB(query);
	}

	private static void insertPayment(int userId, double amount, String method, String status) {
		String query = String.format("INSERT INTO payments(user_id, amount, method, status) VALUES(%d, %f, '%s', '%s')",
				userId, amount, method, status);
		dbUtils.insertIntoDB(query);
	}

	private static void insertReview(int userId, int spaceId, int rating, String comment) {
		String query = String.format("INSERT INTO reviews(user_id, space_id, rating, comment) VALUES(%d, %d, %d, '%s')",
				userId, spaceId, rating, comment);
		dbUtils.insertIntoDB(query);
	}

	private static void insertBookings(int userId, int spaceId, String startDate, String endDate) {
		String query = String.format(
				"INSERT INTO bookings(user_id, space_id, start_date, end_date) VALUES(%d, %d, '%s', '%s')", userId,
				spaceId, startDate, endDate);
		dbUtils.insertIntoDB(query);
	}

	private static void insertSpaces(String name, String location, double price, boolean availability) {
		String query = String.format(
				"INSERT INTO spaces(name, location, price, availability) VALUES('%s', '%s', %f, %b)", name, location,
				price, availability);
		dbUtils.insertIntoDB(query);
	}

	private static void insertUser(String username, String email, String password) {
		String query = String.format("INSERT INTO users(username, email, password) VALUES('%s', '%s', '%s')", username,
				email, password);
		dbUtils.insertIntoDB(query);
	}

	private static void insertAmentites(String name, String description) {
		String query = String.format("INSERT INTO amenities(name, description) VALUES('%s', '%s')", name, description);
		dbUtils.insertIntoDB(query);
	}
}
