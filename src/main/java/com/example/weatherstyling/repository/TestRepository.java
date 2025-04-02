package com.example.weatherstyling.repository;

import com.example.weatherstyling.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {
    // 기본적인 CRUD 기능 제공 (findAll, findById, save, deleteById 등)

    // 추가적으로 name을 기준으로 검색하는 메서드 (선택 사항)

}