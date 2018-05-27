package org.chronology.service;

import java.util.ArrayList;
import java.util.List;

import com.boetzmeyer.chronology.chronology.Branch;
import com.boetzmeyer.chronology.chronology.Commit;
import com.boetzmeyer.chronology.chronology.CommitRelationship;
import com.boetzmeyer.chronology.chronology.IServer;
import com.boetzmeyer.chronology.chronology.ServerFactory;

final class BranchDelta {
	private final IServer server;
	

	public BranchDelta() {
		this.server = ServerFactory.create();
	}

	public List<Commit> getBranchCommits() {
		final List<Commit> commits = new ArrayList<Commit>();
		

		return commits;
	}
	
	public Commit getCommonCommit(final Branch sourceBranch, final Branch targetBranch) {
		final List<Commit> sourceBranchCommits = new ArrayList<Commit>();
		final List<Commit> targetBranchCommits = server.referencesCommitByBranch(targetBranch.getPrimaryKey());
		for (Commit commit : targetBranchCommits) {
			addSourceBranchCommits(sourceBranch, sourceBranchCommits, commit);
		}
		if (sourceBranchCommits.size() > 0) {
			Commit.sortByCommitDate(sourceBranchCommits, false);
			return sourceBranchCommits.get(0);
		}
		return null;
	}

	private void addSourceBranchCommits(final Branch sourceBranch, final List<Commit> sourceBranchCommits, final Commit commit) {
		final List<CommitRelationship> commitRelationships = server.referencesCommitRelationshipByDestination(commit.getPrimaryKey());
		for (CommitRelationship commitRelationship : commitRelationships) {
			final Commit anchestorCommit = commitRelationship.getSourceRef();
			if (anchestorCommit != null) {
				if (anchestorCommit.getBranch() == sourceBranch.getPrimaryKey()) {
					sourceBranchCommits.add(anchestorCommit);
				}
				addSourceBranchCommits(sourceBranch, sourceBranchCommits, anchestorCommit);
			}
		}
	}
}
