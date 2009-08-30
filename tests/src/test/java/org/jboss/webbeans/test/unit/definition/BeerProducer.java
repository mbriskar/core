package org.jboss.webbeans.test.unit.definition;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class BeerProducer
{
   public
   @Produces
   @Named
   Beer getBeerOnTap()
   {
      return new Beer("Dragon's Breathe", "IPA");
   }
   
   public
   @Produces
   @Named
   String getStyle()
   {
      return "Bogus!";
   }
}
