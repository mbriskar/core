package org.jboss.webbeans.test.unit.lookup.circular;

import javax.inject.Inject;

class Farm
{
   
   @Inject
   public Farm(Farm farm)
   {
   }
   
}
