package com.github.almoskvin.urlshortener.service;

import com.github.almoskvin.urlshortener.model.UrlLinker;
import com.github.almoskvin.urlshortener.repository.UrlShortenerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceUnitTest {

    @Mock
    UrlShortenerRepository repo;

    UrlShortenerService service;

    UrlLinker mockLinker;

    @BeforeEach
    public void setUp() {
        service = new UrlShortenerService(repo);
        mockLinker = new UrlLinker("testAlias", "testLink");
    }

    @DisplayName("when getLinkByAlias() then repository.findByAlias() is called")
    @Test
    public void getLinkByAliasRepoCalledTest() {
        String mockAlias = "mockAlias";
        Mockito.when(repo.findByAlias(mockAlias)).thenReturn(null);
        service.getLinkByAlias(mockAlias);
        Mockito.verify(repo, Mockito.times(1)).findByAlias(mockAlias);
    }

    @DisplayName("when getLinkByAlias() and there is no linker, null is returned")
    @Test
    public void getLinkByAliasNullReturnedTest() {
        String mockAlias = "mockAlias";
        Mockito.when(repo.findByAlias(mockAlias)).thenReturn(null);
        String link = service.getLinkByAlias(mockAlias);
        Assertions.assertNull(link);
    }

    @DisplayName("when getLinkByAlias() and there is linker, a link is returned")
    @Test
    public void getLinkByAliasLinkReturnedTest() {
        String mockAlias = "mockAlias";
        Mockito.when(repo.findByAlias(mockAlias)).thenReturn(mockLinker);
        String link = service.getLinkByAlias(mockAlias);
        Assertions.assertEquals(mockLinker.getLink(), link);
    }

    @DisplayName("when findByLink() then repository.findFirstByLink() is called")
    @Test
    public void findByLinkRepoCalledTest() {
        String mockLink = "mockLink";
        Mockito.when(repo.findFirstByLink(mockLink)).thenReturn(null);
        service.findByLink(mockLink);
        Mockito.verify(repo, Mockito.times(1)).findFirstByLink(mockLink);
    }

    @DisplayName("when save() then repository.save() is called")
    @Test
    public void saveRepoCalledTest() {
        Mockito.when(repo.save(mockLinker)).thenReturn(null);
        service.save(mockLinker);
        Mockito.verify(repo, Mockito.times(1)).save(mockLinker);
    }

    @DisplayName("when update() then repository.save() is called")
    @Test
    public void updateRepoCalledTest() {
        Mockito.when(repo.save(mockLinker)).thenReturn(null);
        service.update(mockLinker);
        Mockito.verify(repo, Mockito.times(1)).save(mockLinker);
    }

    @DisplayName("when deleteByAlias() then repository.deleteUrlLinkerByAlias() is called")
    @Test
    public void deleteByAliasRepoCalledTest() {
        String mockAlias = "mockAlias";
        Mockito.doNothing().when(repo).deleteUrlLinkerByAlias(mockAlias);
        service.deleteByAlias(mockAlias);
        Mockito.verify(repo, Mockito.times(1)).deleteUrlLinkerByAlias(mockAlias);
    }

    @DisplayName("when findByAlias() then repository.findByAlias() is called")
    @Test
    public void findByAliasRepoCalledTest() {
        String mockAlias = "mockAlias";
        Mockito.when(repo.findByAlias(Mockito.anyString())).thenReturn(null);
        service.findByAlias(mockAlias);
        Mockito.verify(repo, Mockito.times(1)).findByAlias(Mockito.anyString());
    }

    @DisplayName("when existsByAlias() then repository.existsByAlias() is called")
    @Test
    public void existsByAliasRepoCalledTest() {
        String mockAlias = "mockAlias";
        Mockito.when(repo.existsByAlias(mockAlias)).thenReturn(true);
        service.existsByAlias(mockAlias);
        Mockito.verify(repo, Mockito.times(1)).existsByAlias(mockAlias);
    }

    @DisplayName("when delete() then repository.delete() is called")
    @Test
    public void deleteRepoCalledTest() {
        Mockito.doNothing().when(repo).delete(mockLinker);
        service.delete(mockLinker);
        Mockito.verify(repo, Mockito.times(1)).delete(mockLinker);
    }
}