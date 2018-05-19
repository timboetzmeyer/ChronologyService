package org.chronology.service;

public final class ChronologyServiceFactory {
	private static IChronologyService chronologyService;
	
	private ChronologyServiceFactory() {
	}

	public synchronized static IChronologyService login(final String userName) {
		if (userName == null) {
			throw new ChronologyException("userName is null");
		}
		if (chronologyService == null) {
			chronologyService = new ChronologyService(userName);
		}
		return chronologyService;
	}
}
