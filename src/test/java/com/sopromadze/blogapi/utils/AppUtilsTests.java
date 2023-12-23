package com.sopromadze.blogapi.utils;

import com.sopromadze.blogapi.exception.BlogapiException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AppUtilsTests {

    @Test(expected = BlogapiException.class)
    public void validatePageNumberAndSize_whenPageIsLessThanZero_thenThrowException() {
        try {
            AppUtils.validatePageNumberAndSize(-1, 2);
        } catch (BlogapiException e) {
            Assert.assertEquals(400, e.getStatus().value());
            Assert.assertNotNull(e.getMessage());
            throw e;
        }
    }

    @Test(expected = BlogapiException.class)
    public void validatePageNumberAndSize_whenSizeIsLessThanZero_thenThrowException() {
        try {
            AppUtils.validatePageNumberAndSize(1, -2);
        } catch (BlogapiException e) {
            Assert.assertEquals(400, e.getStatus().value());
            Assert.assertNotNull(e.getMessage());
            throw e;
        }
    }

    @Test(expected = BlogapiException.class)
    public void validatePageNumberAndSize_whenSizeIsGreaterThanMaxSize_thenThrowException() {
        try {
            AppUtils.validatePageNumberAndSize(1, AppConstants.MAX_PAGE_SIZE + 1);
        } catch (BlogapiException e) {
            Assert.assertEquals(400, e.getStatus().value());
            Assert.assertNotNull(e.getMessage());
            throw e;
        }
    }

    @Test
    public void validatePageNumberAndSize_whenPageAndSizeValid_thenDoNothing() {
        AppUtils.validatePageNumberAndSize(1, 2);
    }
}
