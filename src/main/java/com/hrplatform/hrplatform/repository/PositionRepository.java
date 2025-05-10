package com.hrplatform.hrplatform.repository;

import com.hrplatform.hrplatform.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
    // тут можуть бути додаткові методи, якщо потрібно
}
