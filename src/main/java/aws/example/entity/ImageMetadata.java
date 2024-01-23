package aws.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "image")
@Data
public class ImageMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private Long sizeInBytes;

    @Column(name = "file_extension")
    private String fileExtension;

    @Column(name = "last_update",
            columnDefinition = "TIMESTAMP",
            insertable = true,
            updatable = true)
    private LocalDateTime lastUpdate;

}
