/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.application.metrics.aop;

import java.util.Set;

import com.google.common.collect.Sets;

import io.micrometer.core.instrument.Tag;

public class MetricsUtils {

    private static final Tag SUCCESS_STATUS_TAG = Tag.of(
            MetricsConstants.TagKeys.STATUS,
            MetricsConstants.TagValues.SUCCESS
        );
        private static final Tag FAILURE_STATUS_TAG = Tag.of(
            MetricsConstants.TagKeys.STATUS,
            MetricsConstants.TagValues.FAILURE
        );

        /**
         * Utility class private constructor.
         */
        private MetricsUtils() {
        	// Prevents instantiation.
        }

        /**
         * Convenience method that creates a tag set pre-populated with success status.
         *
         * @return a new set containing success tags
         */
        public static Set<Tag> newSuccessTagsSet() {
            final Set<Tag> tags = Sets.newHashSet();
            addSuccessTags(tags);
            
            return tags;
        }

        /**
         * Convenience method to add success tag to an existing set of tags.
         *
         * @param tags the set of tags to add success tags to
         */
        public static void addSuccessTags(final Set<Tag> tags) {
            tags.add(SUCCESS_STATUS_TAG);
        }

        /**
         * Convenience method that creates a tag set pre-populated with failure status and exception details.
         *
         * @param t the exception
         * @return a new set containing failure tags
         */
        public static Set<Tag> newFailureTagsSetForException(final Throwable t) {
            final Set<Tag> tags = Sets.newHashSet();
            addFailureTagsWithException(tags, t);
            
            return tags;
        }

        /**
         * Convenience method to add failure status and exception cause to an existing set of tags.
         *
         * @param tags      the set of existing tags
         * @param throwable the exception to be tagged
         */
        public static void addFailureTagsWithException(
            final Set<Tag> tags,
            final Throwable throwable
        ) {
            tags.add(FAILURE_STATUS_TAG);
            tags.add(Tag.of(MetricsConstants.TagKeys.EXCEPTION_CLASS, throwable.getClass().getCanonicalName()));
        }
}