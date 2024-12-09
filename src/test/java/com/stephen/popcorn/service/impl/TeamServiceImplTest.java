package com.stephen.popcorn.service.impl;

import com.stephen.popcorn.model.dto.team.TeamJoinRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeamServiceImplTest {
	
	@Test
	void joinTeam() {
		TeamJoinRequest teamJoinRequest = new TeamJoinRequest();
		Long teamId = teamJoinRequest.getTeamId();
		String teamPassword = teamJoinRequest.getTeamPassword();
		System.out.println(teamId);
		System.out.println(teamPassword);
		
	}
}