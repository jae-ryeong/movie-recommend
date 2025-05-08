package com.project.movierecommend.repository.jpa;

import com.project.movierecommend.domain.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

}
