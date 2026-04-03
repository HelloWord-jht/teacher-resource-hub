package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.ImportTaskSaveRequest;
import com.teacherresourcehub.entity.Category;
import com.teacherresourcehub.entity.ImportTask;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.entity.ResourceSource;
import com.teacherresourcehub.mapper.CategoryMapper;
import com.teacherresourcehub.mapper.ImportTaskMapper;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.mapper.ResourcePreviewMapper;
import com.teacherresourcehub.mapper.ResourceSourceMapper;
import com.teacherresourcehub.mapper.ResourceStorageMapper;
import com.teacherresourcehub.mapper.ResourceTagMapper;
import com.teacherresourcehub.mapper.TagMapper;
import com.teacherresourcehub.service.impl.ImportTaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImportTaskServiceExecuteTest {

    @Test
    void createShouldInitializePendingStringStatus() {
        ImportTaskMapper importTaskMapper = Mockito.mock(ImportTaskMapper.class);
        TransactionTemplate transactionTemplate = new TransactionTemplate(new NoopTransactionManager());
        ImportTaskService service = new ImportTaskServiceImpl(
                importTaskMapper,
                Mockito.mock(CategoryMapper.class),
                Mockito.mock(TagMapper.class),
                Mockito.mock(ResourceMapper.class),
                Mockito.mock(ResourcePreviewMapper.class),
                Mockito.mock(ResourceTagMapper.class),
                Mockito.mock(ResourceStorageMapper.class),
                Mockito.mock(ResourceSourceMapper.class),
                Mockito.mock(ResourceFileService.class),
                transactionTemplate
        );

        ImportTaskSaveRequest request = new ImportTaskSaveRequest();
        request.setTaskName("导入任务");
        request.setRawPayload("{\"title\":\"家长会资料包\",\"sourceId\":1}");

        ArgumentCaptor<ImportTask> captor = ArgumentCaptor.forClass(ImportTask.class);

        service.create(request);

        verify(importTaskMapper).insert(captor.capture());
        assertThat(captor.getValue().getImportStatus()).isEqualTo("pending");
    }

    @Test
    void executeShouldBindUploadedFilesToGeneratedResourceAndMarkSuccess() {
        ImportTaskMapper importTaskMapper = Mockito.mock(ImportTaskMapper.class);
        CategoryMapper categoryMapper = Mockito.mock(CategoryMapper.class);
        TagMapper tagMapper = Mockito.mock(TagMapper.class);
        ResourceMapper resourceMapper = Mockito.mock(ResourceMapper.class);
        ResourcePreviewMapper resourcePreviewMapper = Mockito.mock(ResourcePreviewMapper.class);
        ResourceTagMapper resourceTagMapper = Mockito.mock(ResourceTagMapper.class);
        ResourceStorageMapper resourceStorageMapper = Mockito.mock(ResourceStorageMapper.class);
        ResourceSourceMapper resourceSourceMapper = Mockito.mock(ResourceSourceMapper.class);
        ResourceFileService resourceFileService = Mockito.mock(ResourceFileService.class);
        TransactionTemplate transactionTemplate = new TransactionTemplate(new NoopTransactionManager());

        ImportTask task = new ImportTask();
        task.setId(88L);
        task.setTaskName("文件导入");
        task.setRawPayload("{\"title\":\"小学家长会PPT资料包\",\"summary\":\"家长会资料\",\"sourceId\":1,\"storagePlatform\":\"baidu_pan\",\"shareUrl\":\"https://example.com\",\"shareCode\":\"ABCD\",\"extractCode\":\"1234\"}");
        task.setImportStatus("pending");
        when(importTaskMapper.selectById(88L)).thenReturn(task);

        ResourceSource source = new ResourceSource();
        source.setId(1L);
        source.setAuthorizationStatus("APPROVED");
        when(resourceSourceMapper.selectById(1L)).thenReturn(source);

        Category category = new Category();
        category.setId(2L);
        category.setCode("JZH");
        category.setName("家长会专区");
        when(categoryMapper.selectOne(any())).thenReturn(category);
        when(tagMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(resourceMapper.selectList(any())).thenReturn(Collections.emptyList());

        doAnswer(invocation -> {
            Resource resource = invocation.getArgument(0);
            resource.setId(101L);
            return 1;
        }).when(resourceMapper).insert(any(Resource.class));

        ImportTaskService service = new ImportTaskServiceImpl(
                importTaskMapper,
                categoryMapper,
                tagMapper,
                resourceMapper,
                resourcePreviewMapper,
                resourceTagMapper,
                resourceStorageMapper,
                resourceSourceMapper,
                resourceFileService,
                transactionTemplate
        );

        service.execute(88L);

        verify(resourceFileService).bindImportTaskFilesToResource(88L, 101L);
        verify(importTaskMapper, Mockito.atLeastOnce()).updateById(task);
        assertThat(task.getImportStatus()).isEqualTo("success");
        assertThat(task.getGeneratedResourceId()).isEqualTo(101L);
        assertThat(task.getGeneratedResourceCode()).startsWith("RES-JZH-");
    }

    private static class NoopTransactionManager implements PlatformTransactionManager {

        @Override
        public TransactionStatus getTransaction(TransactionDefinition definition) {
            return new SimpleTransactionStatus();
        }

        @Override
        public void commit(TransactionStatus status) {
        }

        @Override
        public void rollback(TransactionStatus status) {
        }
    }
}
