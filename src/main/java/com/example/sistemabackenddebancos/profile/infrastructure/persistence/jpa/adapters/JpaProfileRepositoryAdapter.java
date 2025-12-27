package com.example.sistemabackenddebancos.profile.infrastructure.persistence.jpa.adapters;

import com.example.sistemabackenddebancos.profile.domain.model.aggregates.Profile;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.ProfileId;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.UserId;
import com.example.sistemabackenddebancos.profile.domain.repositories.ProfileRepository;
import com.example.sistemabackenddebancos.profile.infrastructure.persistence.jpa.mappers.ProfilePersistenceMapper;
import com.example.sistemabackenddebancos.profile.infrastructure.persistence.jpa.repositories.SpringDataProfileJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaProfileRepositoryAdapter implements ProfileRepository {

    private final SpringDataProfileJpaRepository jpa;

    public JpaProfileRepositoryAdapter(SpringDataProfileJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return jpa.findById(id.value()).map(ProfilePersistenceMapper::toDomain);
    }

    @Override
    public Optional<Profile> findByUserId(UserId userId) {
        return jpa.findByUserId(userId.value()).map(ProfilePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return jpa.existsByUserId(userId.value());
    }

    @Override
    public Profile save(Profile profile) {
        var saved = jpa.save(ProfilePersistenceMapper.toEntity(profile));
        return ProfilePersistenceMapper.toDomain(saved);
    }
}
