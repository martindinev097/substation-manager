package com.buildingenergy.substation_manager.export.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "export_type")
    private ExportType type;

    @Column(name = "export_month")
    private String month;

    private String cloudinaryUrl;

    private LocalDateTime exportedAt;

}
