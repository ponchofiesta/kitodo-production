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

package org.kitodo.data.database.persistence;

import org.kitodo.data.database.beans.LdapServer;
import org.kitodo.data.database.exceptions.DAOException;

import java.util.List;

public class LdapServerDAO extends BaseDAO<LdapServer> {
    @Override
    public LdapServer save(LdapServer ldapGroup) throws DAOException {
        storeObject(ldapGroup);
        return retrieveObject(LdapServer.class, ldapGroup.getId());
    }

    @Override
    public LdapServer getById(Integer id) throws DAOException {
        LdapServer result = retrieveObject(LdapServer.class, id);
        if (result == null) {
            throw new DAOException("Object can not be found in database");
        }
        return result;
    }

    @Override
    public void remove(Integer id) throws DAOException {
        removeObject(LdapServer.class, id);
    }

    @Override
    public List<LdapServer> getAll() {
        return retrieveAllObjects(LdapServer.class);
    }

    @Override
    public List<LdapServer> getAll(int offset, int size) throws DAOException {
        return retrieveObjects("FROM LdapGroup ORDER BY id ASC", offset, size);
    }
}
