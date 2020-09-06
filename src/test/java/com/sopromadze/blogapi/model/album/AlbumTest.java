package com.sopromadze.blogapi.model.album;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.utils.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AlbumTest {

	private Album album;

	@Before
	public void setup() {
		album = TestUtils.createAlbum();
	}

	@Test
	public void getIdTest() {
		Assert.assertNotNull("id can not be null", album.getId());
	}

	@Test
	public void getTitleTest() {
		Assert.assertNotNull("title can not be null", album.getTitle());
	}

	@Test
	public void getUserTest() {
		Assert.assertNotNull("user can not be null", album.getUser());
	}

	@Test
	public void getPhotoTest() {
		Assert.assertNotNull("user can not be null", album.getPhoto());
	}
}
