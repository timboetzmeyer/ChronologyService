package org.chronology.service.examples;

import java.util.List;

import org.chronology.service.ChronologyServiceFactory;
import org.chronology.service.IChronologyService;

import com.boetzmeyer.chronology.chronology.DatabaseModification;
import com.boetzmeyer.chronology.chronology.DatabaseRecord;
import com.boetzmeyer.chronology.chronology.DatabaseSchema;
import com.boetzmeyer.chronology.chronology.DatabaseTable;
import com.boetzmeyer.chronology.chronology.Repository;

public class Example1 {

	public static void main(String[] args) {
		
		// connect your application with the chronology data source
		final IChronologyService chronologyService = ChronologyServiceFactory.login("tb");
		
		// get all repositories that are inside this chronology data source
		final List<Repository> repositories = chronologyService.getRepositories();
		
		for (Repository repository : repositories) {
			
			// get all schemas of this repository
			final List<DatabaseSchema> databaseSchemas = chronologyService.getSchemas(repository);
			
			for (DatabaseSchema databaseSchema : databaseSchemas) {
				
				// get all tables/collections of the schema
				final List<DatabaseTable> databaseTables = chronologyService.getSchemaTables(databaseSchema);
				
				for (DatabaseTable databaseTable : databaseTables) {
					
					// get all records/documents of the table/collection
					final List<DatabaseRecord> databaseRecords = chronologyService.getTableRecords(databaseTable);
					
					for (DatabaseRecord databaseRecord : databaseRecords) {
						
						// get all modifications over all branches
						final List<DatabaseModification> databaseModifications = chronologyService.getRecordHistory(databaseRecord);
						
						for (int i = 0; i < databaseModifications.size(); i++) {
							
							final DatabaseModification databaseModification = databaseModifications.get(i);
							
							// plot all the changes
							System.out.println(databaseModification.toString());
							
						}

					}
					
				}
			}
		}
		
	}

}
