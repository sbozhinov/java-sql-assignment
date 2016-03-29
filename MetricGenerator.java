//Author: Stan Bozhinov. Task 5, cs 327 Fall 2012
//credit: figuring out how to get last letter
//  http://stackoverflow.com/questions/1962181/how-do-i-delete-the-last-character-of-a-particular-string-in-java
// finding percentage in int form: http://stackoverflow.com/questions/7855387/percentage-of-two-int
import java.sql.*;
import java.util.*;

public class MetricGenerator {
	private Connection _connection = null;

	// ---------------------------------------------------------------------------------------------------------------------------$
	public static void main(String[] args) {
		System.out.println("Hi");

		MetricGenerator generator = new MetricGenerator();

		generator.openDatabase();

		generator.dumpLog();

		generator.reportTotalKills();

		generator.reportKillSpellsDan();

		generator.reportKillStreakDan();

		generator.reportMomentum();

		generator.reportTrends();

		generator.closeDatabase();

		System.out.println("Bye");
	}

	// ---------------------------------------------------------------------------------------------------------------------------$
	private void reportTrends() {
		int kills = 0;
		int selfkills = 0;
		int percent;
		int size = 0;
		int[][] arr = new int[10000][4];
		for (int i = 0; i < arr.length; i++) {

			for (int col = 0; col < 4; col++) {
				arr[i][0] = i;

			}
		}
		try {
			Statement statement = _connection.createStatement();

			ResultSet resultSet = statement
					.executeQuery("SELECT * FROM log where actor='Dan' ");
			System.out.println();
			while (resultSet.next()) {

				String action = resultSet.getString("action");
				int cur = resultSet.getInt("minute");
				size = cur;
				if (action.equals("kill")) {
					arr[cur][1]++;
				} else if (action.equals("selfkill")) {

					arr[cur][2]++;
				}

			}// end while
			for (int row = 0; row < 10; row++) {
				if (arr[row][1] == 0 || arr[row][2] == 0) {
					percent = 0;
				} else {
					percent = (int) ((arr[row][2] * 100.0) / arr[row][1]);
				}
				System.out.println((row + 1) + ": K- " + arr[row][1] + " SK- "
						+ arr[row][2] + " PCT-" + percent);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// -----------------------------------------------------------------------------------------------------------------------------
	private void reportMomentum() {

		String mark = "";

		try {
			Statement statement = _connection.createStatement();

			ResultSet resultSet = statement
					.executeQuery("SELECT * FROM log WHERE actor = 'Dan'");

			while (resultSet.next()) {
				String action = resultSet.getString("action");

				if (action.equals("kill")) {
					mark += "#";
					System.out.println(mark);
				}

				if (action.equals("selfkill")) {

					if (mark.length() > 0) {
						mark = mark.substring(0, mark.length() - 1);
						System.out.println(mark);

					} else {
						System.out.println();
					}
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------
	private void reportKillStreakDan() {

		int streak = 0;
		int largestStreak = 0;
		try {
			Statement statement = _connection.createStatement();

			ResultSet resultSet = statement
					.executeQuery("SELECT * FROM log WHERE actor ='Dan'");

			while (resultSet.next()) {
				String action = resultSet.getString("action");

				if (action.equals("kill")) {
					++streak;
					if (streak >= largestStreak) {
						largestStreak = streak;
					}
				} else if (action.equals("selfkill")) {
					streak = 0;
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println();

		System.out.println("Kill streak for Dan: " + largestStreak);

	}

	// -----------------------------------------------------------------------------------------------------------------------------
	private void reportKillSpellsDan() {

		int streak = 0;
		int hot = 0;
		int cold = 0;
		int[] arr = new int[10000];
		int[] prev = new int[10000];
		int count = 0;
		try {
			Statement statement = _connection.createStatement();

			ResultSet resultSet = statement
					.executeQuery("SELECT * FROM log WHERE actor ='Dan' and action ='selfkill'");

			while (resultSet.next()) {
				count++;
				int cur = (resultSet.getInt("minute") * 60)
						+ resultSet.getInt("second");
				if (count == 1) {
					arr[0] = cur;

					prev[0] = cur;
				}

				else {
					prev[count] = cur;

					int diff = cur - prev[count - 1];
					arr[count] = diff;
				}

			}// end while
			Arrays.sort(arr);

			hot = arr[9999 - count + 1];
			cold = arr[9999];

		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println();

		System.out.println("Selfkill hot for Dan: " + hot);
		System.out.println("Selfkill cold for Dan: " + cold);

	}

	// -----------------------------------------------------------------------------------------------------------------------------
	private void reportTotalKills() {
		// this is a standalone example; do not call it from your methods

		int totalKillsDan = 0;
		int totalKillsDave = 0;

		try {
			Statement statement = _connection.createStatement();

			ResultSet resultSet = statement
					.executeQuery("SELECT * FROM log WHERE action = 'kill'");

			while (resultSet.next()) {
				String actor = resultSet.getString("actor");

				if (actor.equals("Dan")) {
					++totalKillsDan;
				} else if (actor.equals("Dave")) {
					++totalKillsDave;
				} else {
					assert false : actor;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("Total Kills for Dan:  " + totalKillsDan);
		System.out.println("Total Kills for Dave: " + totalKillsDave);
	}

	// -----------------------------------------------------------------------------------------------------------------------------
	private void dumpLog() {
		try {
			Statement statement = _connection.createStatement();

			ResultSet resultSet = statement.executeQuery("SELECT * FROM log");

			while (resultSet.next()) {
				int minute = resultSet.getInt("minute");
				int second = resultSet.getInt("second");
				String actor = resultSet.getString("actor");
				String action = resultSet.getString("action");
				String actee = resultSet.getString("actee");
				String weapon = resultSet.getString("weapon");

				System.out.println(minute + ":" + second + " " + actor + " "
						+ action + " " + actee + " " + weapon);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------
	private void openDatabase() {
		try {
			String username = "sbozhinov"; // change this
			String password = "362916"; // change this

			String url = "jdbc:mysql://localhost/zz_cs327_task_4";

			Class.forName("com.mysql.jdbc.Driver").newInstance();

			_connection = DriverManager.getConnection(url, username, password);

			System.out.println("\nDatabase connection established\n");
		} catch (Exception e) {
			System.err.println("Cannot connect to database server " + e);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------
	private void closeDatabase() {
		try {
			_connection.close();

			System.out.println("\nDatabase connection terminated\n");
		} catch (Exception e) {
		}
	}
}
