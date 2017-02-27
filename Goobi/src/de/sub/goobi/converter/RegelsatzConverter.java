/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package de.sub.goobi.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.log4j.Logger;

import de.sub.goobi.beans.Regelsatz;
import de.sub.goobi.helper.exceptions.DAOException;
import de.sub.goobi.persistence.RegelsatzDAO;


public class RegelsatzConverter implements Converter {
   public static final String CONVERTER_ID = "RegelsatzConverter";
   private static final Logger logger = Logger.getLogger(RegelsatzConverter.class);

   @Override
public Object getAsObject(FacesContext context, UIComponent component, String value)
         throws ConverterException {
      if (value == null) {
         return null;
      } else {
         try {
                return new RegelsatzDAO().get(Integer.valueOf(value));
            } catch (NumberFormatException e) {
                logger.error(e);
                return "0";
            } catch (DAOException e) {
                logger.error(e);
                return "0";
            }
      }
   }

   @Override
public String getAsString(FacesContext context, UIComponent component, Object value)
         throws ConverterException {
      if (value == null) {
         return null;
      } else if (value instanceof Regelsatz) {
         return String.valueOf(((Regelsatz) value).getId().intValue());
      } else if (value instanceof String) {
         return (String) value;
      } else {
         throw new ConverterException("Falscher Typ: " + value.getClass() + " muss 'Regelsatz' sein!");
      }
   }

}
