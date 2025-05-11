package com.project.movierecommend.repository.jpa;

import com.project.movierecommend.domain.Jpa.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

}
