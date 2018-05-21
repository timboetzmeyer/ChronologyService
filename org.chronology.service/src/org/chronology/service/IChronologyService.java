package org.chronology.service;

import java.util.List;

import com.boetzmeyer.chronology.chronology.Branch;
import com.boetzmeyer.chronology.chronology.Chronology;
import com.boetzmeyer.chronology.chronology.Commit;
import com.boetzmeyer.chronology.chronology.DatabaseModification;
import com.boetzmeyer.chronology.chronology.DatabaseRecord;
import com.boetzmeyer.chronology.chronology.DatabaseSchema;
import com.boetzmeyer.chronology.chronology.DatabaseTable;
import com.boetzmeyer.chronology.chronology.Repository;
import com.boetzmeyer.chronology.chronology.UserAccount;

public interface IChronologyService {

	List<Repository> getRepositories();

	List<Branch> getRepositoryBranches(Repository repository);

	List<DatabaseModification> getCommitChanges(Commit commit);

	List<DatabaseModification> getRecordHistory(
			DatabaseRecord databaseRecord);

	List<DatabaseTable> getSchemaTables(
			DatabaseSchema databaseSchema);

	List<Commit> getUserCommits(UserAccount user);

	List<Commit> getBranchCommits(Branch branch);

	List<Commit> getUserBranchCommits(UserAccount user,
			Branch branch);

	Chronology getCommitGraph(Commit targetCommit);

	Chronology createModification(String schemaName,
			String tableName, String id, Modification modification,
			String newContent);

	DatabaseTable getTableByName(String schemaName,
			String tableName);

	DatabaseTable getTableByName(DatabaseSchema databaseSchema,
			String tableName);

	DatabaseSchema getSchemaByName(String schemaName);

	Commit commit(Chronology chronology);

	List<DatabaseSchema> getSchemas(Repository repository);

	List<DatabaseRecord> getTableRecords(DatabaseTable databaseTable);

	Branch getCurrentBranch();

}