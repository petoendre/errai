package org.jboss.errai.jpa.test.client;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.jboss.errai.ioc.client.Container;
import org.jboss.errai.jpa.rebind.ErraiEntityManagerGenerator;
import org.jboss.errai.jpa.test.entity.Album;
import org.jboss.errai.jpa.test.entity.Artist;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests the JPA EntityManager facilities provided by Errai JPA.
 * <p>
 * Note that there is a {@link HibernateJpaTest subclass of this test} that runs
 * all the same checks against Hibernate, as a sanity check that we're testing
 * for actual JPA-sanctioned and JPA-compatible behaviour.
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
public class ErraiJpaTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.jpa.test.JpaTest";
  }

  protected EntityManager getEntityManager() {
    JpaTestClient testClient = JpaTestClient.INSTANCE;
    assertNotNull(testClient);
    assertNotNull(testClient.entityManager);
    return JpaTestClient.INSTANCE.entityManager;
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();

    // We need to bootstrap the IoC container manually because GWTTestCase
    // doesn't call onModuleLoad() for us.
    new Container().boostrapContainer();
  }

  /**
   * Tests that the entity manager was injected into the testing class. If this
   * test fails, the likely cause is that the
   * {@link ErraiEntityManagerGenerator} failed to output a compilable class. In
   * that case, try re-running this test with
   * {@code -Derrai.codegen.permissive=true} and
   * {@code -Derrai.codegen.printOut=true}. This should allow you to inspect the
   * generated source code and to see the Java compiler errors.
   */
  public void testEntityManagerInjection() throws Exception {
    getEntityManager(); // has its own assertions
  }

  /**
   * Tests the rejection of a non-entity type.
   */
  public void testPersistNonEntity() {
    try {
      EntityManager em = getEntityManager();
      em.persist("this is a string, not an entity");
      fail();
    } catch (IllegalArgumentException ex) {
      // this is the behaviour we are testing for
    }
  }

  /**
   * Tests the persistence of one entity with no related entities.
   */
  public void testPersistOneAlbum() {

    // make it
    Album album = new Album();
    album.setArtist(null);
    album.setName("Abbey Road");
    album.setReleaseDate(new Date(-8366400000L));

    // store it
    EntityManager em = getEntityManager();
    em.persist(album);
    em.flush();
    em.detach(album);
    assertNotNull(album.getId());

    // fetch it
    Album fetchedAlbum = em.find(Album.class, album.getId());
    assertNotSame(album, fetchedAlbum);
    assertEquals(album.toString(), fetchedAlbum.toString());
  }

  /**
   * Tests the persistence of two unrelated entities of different types.
   */
  public void testPersistOneAlbumAndOneArtist() {

    // make Album (not attached to Artist)
    Album album = new Album();
    album.setArtist(null);
    album.setName("Let It Be");
    album.setReleaseDate(new Date(11012400000L));

    // store them
    EntityManager em = getEntityManager();
    em.persist(album);
    em.flush();

    // make Artist (completely unrelated to Album, but has same numeric ID)
    Artist artist = new Artist();
    artist.setId(album.getId()); // to verify proper separation by entity type
    artist.setName("The Beatles");

    em.persist(artist);
    em.flush();

    em.detach(album);
    em.detach(artist);

    // fetch them
    Album fetchedAlbum = em.find(Album.class, album.getId());
    Artist fetchedArtist = em.find(Artist.class, artist.getId());
    assertNotSame(album, fetchedAlbum);
    assertNotSame(artist, fetchedArtist);

    // ensure Album is intact
    assertEquals(album.toString(), fetchedAlbum.toString());

    // ensure Artist is intact
    assertEquals(artist.toString(), fetchedArtist.toString());
  }

  /**
   * Tests that an entity that was just persisted in this session is always a
   * canonical reference to the same object.
   */
  public void testRetrievePersistedEntity() throws Exception {
    // make it
    Album album = new Album();
    album.setArtist(null);
    album.setName("Abbey Road");
    album.setReleaseDate(new Date(-8366400000L));

    // store it
    EntityManager em = getEntityManager();
    em.persist(album);

    // should come directly from the persistence unit cache at this point
    Album fetchedAlbum = em.find(Album.class, album.getId());
    assertSame(album, fetchedAlbum);

    // ensure it's stored in the database
    em.flush();

    // should still come directly from the persistence unit cache
    fetchedAlbum = em.find(Album.class, album.getId());
    assertSame(album, fetchedAlbum);
  }

  /**
   * Tests that an entity that was fetched in this session is always a canonical
   * reference to the same object.
   */
  public void testRetrieveEntityTwice() throws Exception {
    // make it
    Album album = new Album();
    album.setArtist(null);
    album.setName("Abbey Road");
    album.setReleaseDate(new Date(-8366400000L));

    // store it
    EntityManager em = getEntityManager();
    em.persist(album);

    // ensure it's stored in the database
    em.flush();

    // now forget it
    em.detach(album);

    // multiple fetches should come directly from the persistence unit cache at this point
    Album fetchedAlbum = em.find(Album.class, album.getId());
    Album fetchedAlbum2 = em.find(Album.class, album.getId());
    assertSame(fetchedAlbum, fetchedAlbum2);

    // ensure it's not the original instance we persisted and detached
    assertNotSame(album, fetchedAlbum);
  }

  /**
   * Tests the persistence of two unrelated entities of different types.
   */
  public void testClearDetachesAll() {

    // make Album
    Album album = new Album();
    album.setArtist(null);
    album.setName("Let It Be");
    album.setReleaseDate(new Date(11012400000L));

    // make artist
    Artist artist = new Artist();
    artist.setId(123L);
    artist.setName("The Beatles");

    // store them
    EntityManager em = getEntityManager();
    em.persist(album);
    em.persist(artist);
    em.flush();

    // make sure they're persistent and managed
    assertSame(album, em.find(Album.class, album.getId()));
    assertSame(artist, em.find(Artist.class, artist.getId()));

    em.clear();

    // make sure they were detached
    Album fetchedAlbum = em.find(Album.class, album.getId());
    Artist fetchedArtist = em.find(Artist.class, artist.getId());
    assertNotNull(fetchedAlbum);
    assertNotNull(fetchedArtist);
    assertNotSame(album, fetchedAlbum);
    assertNotSame(artist, fetchedArtist);
  }

  /**
   * Tests the persistence of two unrelated entities of different types.
   */
  public void testRemoveOneEntity() {

    // make Album
    Album album = new Album();
    album.setArtist(null);
    album.setName("Let It Be");
    album.setReleaseDate(new Date(11012400000L));

    // store it
    EntityManager em = getEntityManager();
    em.persist(album);
    em.flush();

    // make sure it's persistent and managed
    assertSame(album, em.find(Album.class, album.getId()));

    // remove it
    em.remove(album);

    // make sure it's gone
    assertNotNull(album.getId());
    assertNull(em.find(Album.class, album.getId()));
  }

  public void testPersistNewEntityLifecycle() throws Exception {

    List<Class<?>> expectedLifecycle = new ArrayList<Class<?>>();

    // make it
    Album album = new Album();
    album.setArtist(null);
    album.setName("Abbey Road");
    album.setReleaseDate(new Date(-8366400000L));

    assertEquals(expectedLifecycle, album.getCallbackLog());

    // store it
    EntityManager em = getEntityManager();
    em.persist(album);

    expectedLifecycle.add(PrePersist.class);
    expectedLifecycle.add(PostPersist.class);
    assertEquals(expectedLifecycle, album.getCallbackLog());

    em.flush();
    assertEquals(expectedLifecycle, album.getCallbackLog());

    // verify that detach causes no lifecycle updates
    em.detach(album);
    assertEquals(expectedLifecycle, album.getCallbackLog());
  }

  public void testFetchEntityLifecycle() throws Exception {

    // make it
    Album album = new Album();
    album.setArtist(null);
    album.setName("Abbey Road");
    album.setReleaseDate(new Date(-8366400000L));

    // store it
    EntityManager em = getEntityManager();
    em.persist(album);
    List<Class<?>> expectedLifecycle = new ArrayList<Class<?>>();
    em.flush();
    em.detach(album);

    // fetch a fresh copy
    Album fetchedAlbum = em.find(Album.class, album.getId());
    expectedLifecycle.add(PostLoad.class);
    assertEquals(expectedLifecycle, fetchedAlbum.getCallbackLog());

    // fetch again; expect no more PostLoad notifications
    Album fetchedAlbum2 = em.find(Album.class, album.getId());
    assertSame(fetchedAlbum, fetchedAlbum2);
    assertEquals(expectedLifecycle, fetchedAlbum2.getCallbackLog());
  }

  public void testRemoveEntityLifecycle() throws Exception {

    // make it
    Album album = new Album();
    album.setArtist(null);
    album.setName("Abbey Road");
    album.setReleaseDate(new Date(-8366400000L));

    // store it
    EntityManager em = getEntityManager();
    em.persist(album);
    List<Class<?>> expectedLifecycle = new ArrayList<Class<?>>();
    em.flush();
    em.detach(album);

    // fetch a fresh copy
    Album fetchedAlbum = em.find(Album.class, album.getId());
    expectedLifecycle.add(PostLoad.class);
    assertEquals(expectedLifecycle, fetchedAlbum.getCallbackLog());

    // delete it
    em.remove(fetchedAlbum);
    em.flush();
    expectedLifecycle.add(PreRemove.class);
    expectedLifecycle.add(PostRemove.class);
    assertEquals(expectedLifecycle, fetchedAlbum.getCallbackLog());

    // verify that detached entity received no further lifecycle updates
    expectedLifecycle.clear();
    expectedLifecycle.add(PrePersist.class);
    expectedLifecycle.add(PostPersist.class);
    assertEquals(expectedLifecycle, album.getCallbackLog());
  }

  // disabled until we support updates to existing entities
  public void IGNOREtestUpdateEntityLifecycle() throws Exception {

    // make it
    Album album = new Album();
    album.setArtist(null);
    album.setName("Abbey Road");
    album.setReleaseDate(new Date(-8366400000L));

    // store it
    EntityManager em = getEntityManager();
    em.persist(album);
    List<Class<?>> expectedLifecycle = new ArrayList<Class<?>>();
    em.flush();

    expectedLifecycle.add(PrePersist.class);
    expectedLifecycle.add(PostPersist.class);
    assertEquals(expectedLifecycle, album.getCallbackLog());

    // modify it
    album.setName("Cowabunga");
    em.flush();

    expectedLifecycle.add(PreUpdate.class);
    expectedLifecycle.add(PostUpdate.class);
    assertEquals(expectedLifecycle, album.getCallbackLog());
  }

}
