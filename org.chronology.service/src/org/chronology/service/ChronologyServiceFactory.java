package org.chronology.service;

public final class ChronologyServiceFactory {
	private static IChronologyService chronologyService;
	
	private ChronologyServiceFactory() {
	}

	public synchronized static IChronologyService login(final SQLConfig sqlConfig, final String userName, final String repositoryName, final String branchName) {
		if (userName == null) {
			throw new ChronologyException("userName is null");
		}
		if (chronologyService == null) {
			chronologyService = new ChronologyService(sqlConfig, null, userName, repositoryName, branchName);
		}
		return chronologyService;
	}
	
	public synchronized static IChronologyService login(final FileConfig fileConfig, final String userName, final String repositoryName, final String branchName) {
		if (userName == null) {
			throw new ChronologyException("userName is null");
		}
		if (chronologyService == null) {
			chronologyService = new ChronologyService(null, fileConfig, userName, repositoryName, branchName);
		}
		return chronologyService;
	}

}
