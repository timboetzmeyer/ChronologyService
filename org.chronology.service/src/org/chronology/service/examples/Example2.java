package org.chronology.service.examples;

import org.chronology.service.ChronologyServiceFactory;
import org.chronology.service.IChronologyService;
import org.chronology.service.Modification;

import com.boetzmeyer.chronology.chronology.Chronology;
import com.boetzmeyer.chronology.chronology.Commit;

public class Example2 {

	public static void main(String[] args) {
		
		// connect your application with the chronology data source
		final IChronologyService chronologyService = ChronologyServiceFactory.login("User B", "MyRepository", "Branch-2");
		
		// create two modifications on the branch data
		final Chronology change1 = chronologyService.createModification("MySchema", "Customer", "MyID_1", Modification.MODIFIED, "changed content");
		final Chronology change2 = chronologyService.createModification("MySchema", "Customer", "MyID_2", Modification.DELETED, null);
		
		// summarize all modifications in change1
		change1.add(change2);
		
		// commit the modifications on the current branch
		final Commit commit = chronologyService.commit(change1);
		
		// print the commit timestamp
		System.out.println(commit.toString());		
	}

}
