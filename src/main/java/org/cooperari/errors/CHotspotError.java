package org.cooperari.errors;

import java.util.Arrays;
import java.util.Collection;

import org.cooperari.CAlways;
import org.cooperari.CNever;
import org.cooperari.CSometimes;

/**
 * Exception thrown due to hotspot reachability errors.
 * 
 * @since 0.2
 */
@SuppressWarnings("serial")
public final class CHotspotError extends Error {

  /**
   * Constructs a new error for given hotspot reachability annotation and a collection of related hotspots.
   * @param annotation Annotation class (one of {@link CAlways}, {@link CSometimes}, or {@link CNever}).
   * @param hotspots Hotspot collection.
   */
  public CHotspotError(Class<?> annotation, Collection<String> hotspots) {
    super(formatMessage(annotation,hotspots));
  }

  /**
   * Constructs a new error for given reachability annotation and specific hotspot.
   * @param annotation Annotation class (one of {@link CAlways}, {@link CSometimes}, {@link CNever}).
   * @param hotspot Hotspot collection.
   */
  public CHotspotError(Class<?> annotation, String hotspot) {
    this(annotation, Arrays.asList(hotspot));
  }

  @SuppressWarnings("javadoc")
  private static String formatMessage(Class<?> clazz, Collection<String> hotspots) {
    StringBuilder sb = new StringBuilder();
    sb.append(clazz.getSimpleName())
    .append(' ')
    .append('{').append(' ');
    for (String h : hotspots) {
      sb.append('\"').append(h).append('\"').append(' ');
    }
    return sb.append('}').toString();
  }
}
