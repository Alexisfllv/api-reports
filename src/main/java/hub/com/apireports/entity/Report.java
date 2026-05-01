package hub.com.apireports.entity;

import hub.com.apireports.entity.enums.PriorityLevel;
import hub.com.apireports.entity.enums.RegionType;
import hub.com.apireports.entity.enums.ReportStatus;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.util.auditing.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_report_region", columnList = "region"),
        @Index(name = "idx_report_province", columnList = "province"),
        @Index(name = "idx_report_district", columnList = "district")
})
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime incidentDate;

    @Column(nullable = false)
    private LocalDateTime reportDate;

    @Column(nullable = false,length = 20)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private RegionType region;

    @Column(nullable = false,length = 30)
    private String province;

    @Column(nullable = false,length = 30)
    private String district;

    @Column(nullable = false,length = 300)
    private String address;

    @Column(nullable = false, length = 300)
    private String reference;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PriorityLevel priorityLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_report_category"))
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_report_member"))
    private Member member;

    // List
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrackingHistory> trackingHistory = new ArrayList<>();

}
