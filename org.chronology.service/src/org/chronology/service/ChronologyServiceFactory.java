package org.chronology.service;

public final class ChronologyServiceFactory {
	private static IChronologyService chronologyService;
	
	private ChronologyServiceFactory() {
	}

	public synchronized static IChronologyService login(final String userName, final String repositoryName, final String branchName) {
		if (userName == null) {
			throw new ChronologyException("userName is null");
		}
		if (chronologyService == null) {
			chronologyService = new ChronologyService(userName, repositoryName, branchName);
		}
		return chronologyService;
	}
}
