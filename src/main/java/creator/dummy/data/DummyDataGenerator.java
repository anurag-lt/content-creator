package creator.dummy.data;

import com.github.javafaker.Faker;
import creator.utils.DBUtils;

import java.util.concurrent.TimeUnit;

public class DummyDataGenerator {
	private static Faker faker = new Faker();
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
	private static void insertAmentites() {
		String name = faker.lorem().word();
		String description = faker.lorem().sentence();

		String query = String.format("INSERT INTO amenities(amenityname, description) VALUES('%s', '%s')", name, description);
		dbUtils.insertIntoDB(query);
	}

	private static void insertUser() {
		String username = faker.name().username();
		String email = faker.internet().emailAddress();
		String password = "hashed_password";

		String query = String.format("INSERT INTO users(username, email, passwordhash) VALUES('%s', '%s', '%s')", username, email, password);
		dbUtils.insertIntoDB(query);
	}

	private static void insertSpaces() {
		String name = faker.company().name();
		String location = faker.address().cityName();
		double price = faker.number().randomDouble(2, 50, 500);
		boolean availability = faker.random().nextBoolean();

		String query = String.format("INSERT INTO spaces(spacename, location, priceperhour, availability) VALUES('%s', '%s', %f, %b)", name, location, price, availability);
		dbUtils.insertIntoDB(query);
	}

	private static void insertBookings() {
		int userId = faker.number().numberBetween(1, 100); // Assuming user IDs are between 1 and 100
		int spaceId = faker.number().numberBetween(1, 100); // Assuming space IDs are between 1 and 100
		String startDate = faker.date().future(30, TimeUnit.DAYS).toString();
		String endDate = faker.date().future(60, TimeUnit.DAYS).toString();

		String query = String.format("INSERT INTO bookings(creatorid, spaceid, starttime, endtime) VALUES(%d, %d, '%s', '%s')", userId, spaceId, startDate, endDate);
		dbUtils.insertIntoDB(query);
	}

	private static void insertReview() {
		int userId = faker.number().numberBetween(1, 100); // Assuming user IDs are between 1 and 100
		int spaceId = faker.number().numberBetween(1, 100); // Assuming space IDs are between 1 and 100
		int rating = faker.number().numberBetween(1, 5);
		String comment = faker.lorem().sentence();

		String query = String.format("INSERT INTO reviews(creatorid, spaceid, rating, reviewtext) VALUES(%d, %d, %d, '%s')", userId, spaceId, rating, comment);
		dbUtils.insertIntoDB(query);
	}

	private static void insertPayment() {
		int userId = faker.number().numberBetween(1, 100); // Assuming user IDs are between 1 and 100
		double amount = faker.number().randomDouble(2, 10, 500);
		String method = faker.lorem().word();
		String status = faker.lorem().word();

		String query = String.format("INSERT INTO payments(creatorid, amount, method, status) VALUES(%d, %f, '%s', '%s')", userId, amount, method, status);
		dbUtils.insertIntoDB(query);
	}

	private static void insertspaceamenities() {
		int spaceId = faker.number().numberBetween(1, 100); // Assuming space IDs are between 1 and 100
		int amenityId = faker.number().numberBetween(1, 100); // Assuming amenity IDs are between 1 and 100

		String query = String.format("INSERT INTO space_amenities(space_id, amenity_id) VALUES(%d, %d)", spaceId, amenityId);
		dbUtils.insertIntoDB(query);
	}

	private static void insertspaceimages() {
		int spaceId = faker.number().numberBetween(1, 100); // Assuming space IDs are between 1 and 100
		String imageUrl = faker.internet().url();

		String query = String.format("INSERT INTO space_images(space_id, image_url) VALUES(%d, '%s')", spaceId, imageUrl);
		dbUtils.insertIntoDB(query);
	}
}
