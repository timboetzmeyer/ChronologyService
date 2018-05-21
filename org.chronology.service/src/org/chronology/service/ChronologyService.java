package org.chronology.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.boetzmeyer.chronology.chronology.Branch;
import com.boetzmeyer.chronology.chronology.ChangeType;
import com.boetzmeyer.chronology.chronology.Chronology;
import com.boetzmeyer.chronology.chronology.Commit;
import com.boetzmeyer.chronology.chronology.CommitRelationship;
import com.boetzmeyer.chronology.chronology.DatabaseKey;
import com.boetzmeyer.chronology.chronology.DatabaseModification;
import com.boetzmeyer.chronology.chronology.DatabaseRecord;
import com.boetzmeyer.chronology.chronology.DatabaseSchema;
import com.boetzmeyer.chronology.chronology.DatabaseTable;
import com.boetzmeyer.chronology.chronology.IServer;
import com.boetzmeyer.chronology.chronology.Repository;
import com.boetzmeyer.chronology.chronology.ServerFactory;
import com.boetzmeyer.chronology.chronology.UserAccount;

final class ChronologyService implements IChronologyService {	
	private static final int INITIAL_RECORD_VERSION = 1;
	
	private final IServer server;
	private final UserAccount userAccount;
	private final Repository repository;
	private final Branch currentBranch;
	
	protected ChronologyService(final String userName, final String repositoryName, final String branchName) {
		if (userName == null) {
			throw new ChronologyException("userAccount is null");
		}
		if (repositoryName == null) {
			throw new ChronologyException("repositoryName is null");
		}
		if (branchName == null) {
			throw new ChronologyException("branchName is null");
		}
		server = ServerFactory.create();
		
		userAccount = this.findUserByName(userName);
		if (userAccount == null) {
			throw new ChronologyException(String.format("User with name '%s' could not be found in the chronology model", userName));
		}
		
		repository = this.findRepositoryByName(repositoryName);
		if (repository == null) {
			throw new ChronologyException(String.format("Repository with name '%s' could not be found in the chronology model", repositoryName));
		}
		
		currentBranch = this.findBranchByName(repository, branchName);
		if (currentBranch == null) {
			throw new ChronologyException(String.format("Branch with name '%s' could not be found in the chronology model", branchName));
		}		
	}

	private Branch findBranchByName(final Repository repository, final String branchName) {
		if (repository == null) {
			throw new ChronologyException("repository is null");
		}
		if (branchName == null) {
			throw new ChronologyException("branchName is null");
		}
		List<Branch> branches = server.referencesBranchByRepository(repository.getPrimaryKey());
		for (Branch branch : branches) {
			if (branch.getBranchName().equalsIgnoreCase(branchName)) {
				return branch;
			}
		}
		return null;
	}

	private UserAccount findUserByName(final String userName) {
		if (userName == null) {
			throw new ChronologyException("userAccount is null");
		}
		List<UserAccount> users = server.listUserAccount();
		for (UserAccount userAccount : users) {
			if (userAccount.getUserName().equalsIgnoreCase(userName)) {
				return userAccount;
			}
		}
		return null;
	}

	private Repository findRepositoryByName(final String repositoryName) {
		if (repositoryName == null) {
			throw new ChronologyException("repositoryName is null");
		}
		List<Repository> repositories = server.listRepository();
		for (Repository repository : repositories) {
			if (repository.getRepositoryName().equalsIgnoreCase(repositoryName)) {
				return repository;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getRepositories()
	 */
	@Override
	@ReadOnly
	public List<Repository> getRepositories() {
		return server.listRepository();
	}
	
	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getRepositoryBranches(com.boetzmeyer.chronology.chronology.Repository)
	 */
	@Override
	@ReadOnly
	public List<Branch> getRepositoryBranches(final Repository repository) {
		if (repository == null) {
			throw new ChronologyException("Parameter is null");
		}
		return server.referencesBranchByRepository(repository.getPrimaryKey());
	}
	
	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getCommitChanges(com.boetzmeyer.chronology.chronology.Commit)
	 */
	@Override
	@ReadOnly
	public List<DatabaseModification> getCommitChanges(final Commit commit) {
		if (commit == null) {
			throw new ChronologyException("Parameter is null");
		}
		return server.referencesDatabaseModificationByCommit(commit.getPrimaryKey());
	}
	
	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getRecordHistory(com.boetzmeyer.chronology.chronology.DatabaseRecord)
	 */
	@Override
	@ReadOnly
	public List<DatabaseModification> getRecordHistory(final DatabaseRecord databaseRecord) {
		if (databaseRecord == null) {
			throw new ChronologyException("databaseRecord is null");
		}		
		final List<DatabaseModification> recordVersions = server.referencesDatabaseModificationByDatabaseRecord(databaseRecord.getPrimaryKey());
		DatabaseModification.sortByModificationTime(recordVersions, false);
		return recordVersions;
	}
	
	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getSchemaTable(com.boetzmeyer.chronology.chronology.DatabaseSchema)
	 */
	@Override
	@ReadOnly
	public List<DatabaseTable> getSchemaTables(final DatabaseSchema databaseSchema) {
		if (databaseSchema == null) {
			throw new ChronologyException("Parameter is null");
		}
		final List<DatabaseTable> tables = server.referencesDatabaseTableByDatabaseSchema(databaseSchema.getPrimaryKey());
		DatabaseTable.sortByTableName(tables, true);
		return tables;
	}
	
	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getUserCommits(com.boetzmeyer.chronology.chronology.UserAccount)
	 */
	@Override
	@ReadOnly
	public List<Commit> getUserCommits(final UserAccount user) {
		if (user == null) {
			throw new ChronologyException("Parameter is null");
		}
		final List<Commit> commits = server.referencesCommitByUserAccount(user.getPrimaryKey());
		Commit.sortByCommitDate(commits, false);
		return commits;
	}
		
	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getBranchCommits(com.boetzmeyer.chronology.chronology.Branch)
	 */
	@Override
	@ReadOnly
	public List<Commit> getBranchCommits(final Branch branch) {
		if (branch == null) {
			throw new ChronologyException("Parameter is null");
		}
		final List<Commit> commits = server.referencesCommitByBranch(branch.getPrimaryKey());
		Commit.sortByCommitDate(commits, false);
		return commits;
	}
	
	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getUserBranchCommits(com.boetzmeyer.chronology.chronology.UserAccount, com.boetzmeyer.chronology.chronology.Branch)
	 */
	@Override
	@ReadOnly
	public List<Commit> getUserBranchCommits(final UserAccount user, final Branch branch) {
		if (user == null) {
			throw new ChronologyException("user is null");
		}
		final List<Commit> userBranchCommits = new ArrayList<Commit>();
		final List<Commit> commits = getBranchCommits(branch);
		for (Commit commit : commits) {
			if (commit.getUserAccount() == user.getPrimaryKey()) {
				userBranchCommits.add(commit);
			}
		}
		Commit.sortByCommitDate(userBranchCommits, false);
		return userBranchCommits;
	}

	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getCommitGraph(com.boetzmeyer.chronology.chronology.Commit)
	 */
	@Override
	@ReadOnly
	public Chronology getCommitGraph(final Commit targetCommit) {
		if (targetCommit == null) {
			throw new ChronologyException("Parameter is null");
		}
		final Chronology chronology = Chronology.createEmpty();
		addCommit(targetCommit, chronology);
		
		return chronology;
	}

	@Offline
	private void addCommit(final Commit commit, final Chronology chronology) {
		chronology.addCommit(commit);
		final List<CommitRelationship> anchestorCommits = server.referencesCommitRelationshipByDestination(commit.getPrimaryKey());
		for (CommitRelationship commitRelationship : anchestorCommits) {
			chronology.addCommitRelationship(commitRelationship);
			final Commit anchestorCommit = commitRelationship.getSourceRef();
			if (anchestorCommit != null) {
				addCommit(anchestorCommit, chronology);
			}
		}
	}
		
	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#createModification(java.lang.String, java.lang.String, java.lang.String, org.chronology.service.Modification, java.lang.String)
	 */
	@Override
	@Offline
	public Chronology createModification(final String schemaName, final String tableName, final String id, final Modification modification, final String newContent) {
		if (schemaName == null) {
			throw new ChronologyException("schemaName is null");
		}
		if (tableName == null) {
			throw new ChronologyException("tableName is null");
		}
		if (modification == null) {
			throw new ChronologyException("modification is null");
		}
		if (newContent == null) {
			throw new ChronologyException("newContent is null");
		}
		final ChangeType changeType = this.getChangeType(modification.name());
		if (changeType == null) {
			throw new ChronologyException("changeType '%s' is not available");
		}
		
		final Chronology chronology = Chronology.createEmpty();

		final DatabaseSchema databaseSchema = getSchemaByName(schemaName);
		final DatabaseTable databaseTable = getTableByName(databaseSchema, tableName);
		final DatabaseKey databaseKey;
		if (modification.equals(Modification.ADDED)) {
			databaseKey = DatabaseKey.generate();
			databaseKey.setTableKey(id);
			chronology.addDatabaseKey(databaseKey);
		} else {
			databaseKey = this.getKeyByID(databaseTable, id);
		}

		final DatabaseRecord databaseRecord = DatabaseRecord.generate();
		databaseRecord.setDatabaseKey(databaseKey.getPrimaryKey());
		databaseRecord.setDatabaseTable(databaseTable.getPrimaryKey());
		databaseRecord.setRecordVersion(INITIAL_RECORD_VERSION);
		chronology.addDatabaseRecord(databaseRecord);
				
		final DatabaseModification databaseModification = DatabaseModification.generate();
		databaseModification.setModificationTime(new Date());
		databaseModification.setChangeType(changeType.getPrimaryKey());
		databaseModification.setNewContent(newContent);		
		databaseModification.setDatabaseRecord(databaseRecord.getPrimaryKey());
		chronology.addDatabaseModification(databaseModification);
		
		return chronology;
	}
	
	// TODO performance has to be tuned
	@ReadOnly
	private DatabaseKey getKeyByID(final DatabaseTable databaseTable, final String id) {
		if (databaseTable == null) {
			throw new ChronologyException("databaseTable is null");
		}
		if (id == null) {
			throw new ChronologyException("id is null");
		}
		final List<DatabaseRecord> databaseRecords = server.referencesDatabaseRecordByDatabaseTable(databaseTable.getPrimaryKey());
		for (DatabaseRecord databaseRecord : databaseRecords) {
			final DatabaseKey databaseKey = databaseRecord.getDatabaseKeyRef();
			if (databaseKey != null) {
				if (databaseKey.getTableKey().equals(id)) {
					return databaseKey;
				}
			}
		}
		throw new ChronologyException("id is null");
	}

	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getTableByName(java.lang.String, java.lang.String)
	 */
	@Override
	@ReadOnly
	public DatabaseTable getTableByName(final String schemaName, final String tableName) {
		if (schemaName == null) {
			throw new ChronologyException("schemaName is null");
		}
		if (tableName == null) {
			throw new ChronologyException("tableName is null");
		}
		final DatabaseSchema databaseSchema = getSchemaByName(schemaName);
		return this.getTableByName(databaseSchema, tableName);
	}
	
	
	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getTableByName(com.boetzmeyer.chronology.chronology.DatabaseSchema, java.lang.String)
	 */
	@Override
	public DatabaseTable getTableByName(final DatabaseSchema databaseSchema, final String tableName) {
		if (databaseSchema == null) {
			throw new ChronologyException("databaseSchema is null");
		}
		if (tableName == null) {
			throw new ChronologyException("tableName is null");
		}
		final List<DatabaseTable> tables = server.referencesDatabaseTableByDatabaseSchema(databaseSchema.getPrimaryKey());
		for (DatabaseTable table : tables) {
			if (tableName.equalsIgnoreCase(table.getTableName())) {
				return table;
			}
		}
		throw new ChronologyException(String.format("Table with name '%s' can not be found in the database schema '%s'", tableName, databaseSchema.toString()));
	}
	
	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#getSchemaByName(java.lang.String)
	 */
	@Override
	public DatabaseSchema getSchemaByName(final String schemaName) {
		if (schemaName == null) {
			throw new ChronologyException("schemaName is null");
		}
		final List<DatabaseSchema> schemas = server.listDatabaseSchema();
		for (DatabaseSchema databaseSchema : schemas) {
			if (schemaName.equalsIgnoreCase(databaseSchema.getSchemaName())) {
				return databaseSchema;
			}
		}
		throw new ChronologyException(String.format("Schema with name '%s' can not be found in the chronology model", schemaName));
	}

	/* (non-Javadoc)
	 * @see org.chronology.service.IChronologyService#commit(com.boetzmeyer.chronology.chronology.Chronology)
	 */
	@Override
	public Commit commit(final Chronology chronology) {
		final Branch branch = this.getCurrentBranch();
		if (branch == null) {
			throw new ChronologyException("current branch is null");
		}
		if (chronology == null) {
			throw new ChronologyException("chronology is null");
		}
		
		final List<DatabaseModification> databaseModifications = chronology.listDatabaseModification();
		if (databaseModifications.size() == 0) {
			throw new ChronologyException("No database modifications to commit");
		}

		final Chronology ta = Chronology.createEmpty();
		
		final Commit commit = Commit.generate();
		commit.setBranch(branch.getPrimaryKey());
		commit.setCommitDate(new Date());
		commit.setUserAccount(this.userAccount.getPrimaryKey());
		ta.addCommit(commit);
		
		for (DatabaseModification databaseModification : databaseModifications) {
			databaseModification.setCommit(commit.getPrimaryKey());
			ta.addDatabaseModification(databaseModification);
		}
		
		final List<Commit> branchCommits = server.referencesCommitByBranch(branch.getPrimaryKey());
		if (branchCommits.size() > 0) {
			Commit.sortByCommitDate(branchCommits, false);
			final CommitRelationship commitRelationship = CommitRelationship.generate();
			commitRelationship.setSource(branchCommits.get(0).getPrimaryKey());
			commitRelationship.setDestination(commit.getPrimaryKey());
			ta.addCommitRelationship(commitRelationship);
		}

		if (ta.save()) {
			return commit;
		}
		throw new ChronologyException("Commit could not be tracked in chronology");
	}
		
	private ChangeType getChangeType(final String title) {
		final List<ChangeType> changeTypes = server.listChangeType();
		for (int i = 0; i < changeTypes.size(); i++) {
			if (title.equalsIgnoreCase(changeTypes.get(i).getChangeTitle())) {
				return changeTypes.get(i);
			}
		}
		return null;
	}

	@Override
	public List<DatabaseSchema> getSchemas(final Repository repository) {
		if (repository == null) {
			throw new ChronologyException("repository is null");
		}
		return server.referencesDatabaseSchemaByRepository(repository.getPrimaryKey());
	}

	@Override
	public List<DatabaseRecord> getTableRecords(final DatabaseTable databaseTable) {
		if (databaseTable == null) {
			throw new ChronologyException("databaseTable is null");
		}
		return server.referencesDatabaseRecordByDatabaseTable(databaseTable.getPrimaryKey());
	}

	@Override
	public Branch getCurrentBranch() {
		return this.currentBranch.copy();
	}

}
