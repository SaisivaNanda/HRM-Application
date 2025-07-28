package com.hrms.project.repository;

import com.hrms.project.entity.DegreeCertificates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DegreeCertificatesRepository extends JpaRepository<DegreeCertificates,Long> {
    List<DegreeCertificates> findByEmployee_EmployeeId(String employeeId);
}