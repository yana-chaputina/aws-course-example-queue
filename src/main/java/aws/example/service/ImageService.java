package aws.example.service;


import aws.example.dto.ImageMetadataDto;
import aws.example.dto.ImageUploadDto;
import aws.example.entity.ImageMetadata;
import aws.example.exception.NotFoundException;
import aws.example.exception.S3ObjectNotFoundException;
import aws.example.orika.ImageMetadataMapper;
import aws.example.repository.ImageRepository;
import com.amazonaws.util.EC2MetadataUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.List;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    private final S3Service s3Service;

    private final FileService fileService;

    private final NotificationService notificationService;

    @Autowired
    public ImageService(ImageRepository imageRepository,
                        S3Service s3Service,
                        FileService fileService,
                        NotificationService notificationService) {
        this.imageRepository = imageRepository;
        this.s3Service = s3Service;
        this.fileService = fileService;
        this.notificationService = notificationService;
    }

    @Transactional
    public ImageMetadataDto getRandomMetadata() {
        return ImageMetadataMapper.getDtoFromEntity(
                imageRepository.findRandomEntity()
                        .orElseThrow(() -> new NotFoundException("Random image not found")));
    }

    @Transactional
    public ImageMetadataDto upload(ImageUploadDto imageUploadDto) {
        MultipartFile file = imageUploadDto.getFile();
        InputStream inputStream = fileService.getInputStream(file);
        s3Service.uploadObject(inputStream, file.getOriginalFilename(), imageUploadDto.getName());
        ImageMetadata imageMetadata = new ImageMetadata();
        imageMetadata.setName(imageUploadDto.getName());
        imageMetadata.setSizeInBytes(file.getSize());
        imageMetadata.setFileExtension(fileService.getFileExtension(file.getOriginalFilename()));
        notificationService.sendMessageToQueue(createMessage(imageMetadata));
        return ImageMetadataMapper.getDtoFromEntity(imageRepository.save(imageMetadata));
    }

    private String createMessage(ImageMetadata imageMetadata) {
        return StringUtils.joinWith("\n", "Image was uploaded: ",
                "Name: "+imageMetadata.getName(),
                "Extension: "+imageMetadata.getFileExtension(),
                "Size: "+imageMetadata.getSizeInBytes());
    }

    @Transactional
    public List<ImageMetadataDto> findAll() {
        return ImageMetadataMapper.getListDtoFromEntity(imageRepository.findAll());
    }

    @Transactional
    public void delete(String name) {
        List<ImageMetadata> imageMetadataList = imageRepository.findByName(name);
        imageMetadataList.stream()
                .map(ImageMetadata::getName)
                .forEach(s3Service::deleteObject);
        imageRepository.deleteAll(imageMetadataList);
    }

    @Transactional
    public byte[] getImage(String name) {
        List<ImageMetadata> imageMetadataList = imageRepository.findByName(name);
        if (imageMetadataList == null || imageMetadataList.size() == 0) {
            throw new S3ObjectNotFoundException("Image with this name not found");
        }
        return s3Service.downloadObject(imageMetadataList.get(0).getName());
    }
}
