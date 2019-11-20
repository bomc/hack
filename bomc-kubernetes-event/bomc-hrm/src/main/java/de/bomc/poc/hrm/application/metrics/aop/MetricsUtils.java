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

//	private static final Map<AnnotationCacheKey, Metric> findAnnotationCache = new ConcurrentReferenceHashMap<>(256);
//
//	/**
//	 * Given a {@link MethodInvocation} for a method or class annotated with
//	 * {@link Metric @Metric}, this method constructs a merged representation of the
//	 * method and class level annotations. Especially the {@link Metric#name()} will
//	 * be merged. Resolved annotation will be cached, the merge step only happens
//	 * the first time an annotation is looked up.
//	 *
//	 * @param method The method annotated with {@link Metric @Metric}.
//	 * @param target The target object annotated with {@link Metric @Metric}.
//	 * @return An instance of {@link Metric} with method and class attributes
//	 *         merged.
//	 */
//	public static Metric getMergedMetricAnnotation(final Method method, final Object target) {
//
//		final AnnotationCacheKey cacheKey = new AnnotationCacheKey(method, Metric.class);
//
//		return findAnnotationCache.computeIfAbsent(cacheKey,
//		        annotationCacheKey -> synthesizeAnnotation(method, target));
//	}
//
//	private static Metric synthesizeAnnotation(final Method method, final Object target) {
//		Map<String, Object> attributeMap = new HashMap<>();
//
//		final Optional<Metric> methodAnnotation = Optional.ofNullable(AnnotationUtils.findAnnotation(method, Metric.class));
//		final Optional<Metric> classAnnotation = Optional
//		        .ofNullable(AnnotationUtils.findAnnotation(target.getClass(), Metric.class));
//
//		final String classKey = classAnnotation.map(Metric::name).filter(StringUtils::hasText).orElse(getClassName(method));
//		final String methodKey = methodAnnotation.map(Metric::name).filter(StringUtils::hasText).orElse(method.getName());
//
//		final String metricName = new StringBuilder().append(wrapName(StringUtils.uncapitalize(classKey)))
//		        .append(StringUtils.uncapitalize(methodKey)).toString();
//		attributeMap.put("name", metricName);
//		
//		return AnnotationUtils.synthesizeAnnotation(attributeMap, Metric.class, method);
//	}
//
//	private static String wrapName(String name) {
//		
//		if (StringUtils.hasText(name) && !name.endsWith(".")) {
//			name += ".";
//		}
//		
//		return name;
//	}
//
//	private static String getClassName(final Method method) {
//		
//		return method.getDeclaringClass().getSimpleName();
//	}

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