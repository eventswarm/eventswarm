package com.eventswarm.channels;

/**
 *
 * Simple interface to peek at data and return a discriminator that can be used to select the event construction
 * method (e.g. if JSON is from twitter, the discriminator might return the string 'twitter'.
 *
 * The type D is the class of the discriminator (typically a string) and T is the raw data type from which the
 * discriminator is extracted.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public interface Discriminator<D,T> {
   public D getDiscriminator(T data);
}
