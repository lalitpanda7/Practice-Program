package com.project.set_up.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.set_up.component.Builds;
import com.project.set_up.component.Versions;

public interface BuildsRepository extends JpaRepository<Builds, Integer> {
  //  public List<Builds> findAllByVersion(String id, Sort by);

    public List<Builds> findAllByVersionAndDbUpdatedDateIsNull(Versions version, Sort by);

    public List<Builds> findAllByVersionAndDbUpdatedDateIsNotNull(Versions version, Sort by);
}
