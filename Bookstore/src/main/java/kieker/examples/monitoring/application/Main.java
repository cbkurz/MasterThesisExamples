package kieker.examples.monitoring.application;

public final class Main {

	private Main() {}

	public static void main(final String[] args) {
		final Bookstore bookstore = new Bookstore();
		bookstore.searchBook();
	}
}
