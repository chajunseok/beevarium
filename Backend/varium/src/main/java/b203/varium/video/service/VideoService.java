// VideoService.java
package b203.varium.video.service;

import b203.varium.broadcastStation.repository.BroadcastStationRepository;
import b203.varium.video.entity.FileEntity;
import b203.varium.video.repository.FileInfoRepository;
import b203.varium.video.repository.VideoRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoService {

    private final VideoRepository videoRepository;
    private final FileInfoRepository fileInfoRepository;
    private final BroadcastStationRepository broadcastStationRepository;
    private final AmazonS3 amazonS3Client;

    @Transactional
    public void saveFile(MultipartFile file, String videoType, int videoNo) throws IOException {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setOriginName(file.getOriginalFilename());
        String originalFileName = fileEntity.getOriginName();
        String saveFileName = null;
        String today = new SimpleDateFormat("yyMMdd").format(new Date());

        if (originalFileName != null && !originalFileName.isEmpty()) {
            saveFileName = UUID.randomUUID()
                    + originalFileName.substring(originalFileName.lastIndexOf('.'));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(
                    new PutObjectRequest("b203-beevarium", videoType + "/" + today + "/" + saveFileName, file.getInputStream(), metadata));

            fileEntity.setFilePath(today);
            fileEntity.setSavedName(saveFileName);
        }

        fileEntity.setVideo(videoRepository.findById(videoNo));
        fileInfoRepository.save(fileEntity);
    }
}