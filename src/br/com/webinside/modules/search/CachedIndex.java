/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Soluções Tecnológicas Ltda.
 * Copyright (c) 2009-2010 Incógnita Inteligência Digital Ltda.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; versão 2.1 da Licença.
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 * ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Você deve ter recebido uma cópia da GNU LGPL junto com este programa; se não, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.modules.search;

import org.apache.lucene.index.IndexReader;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CachedIndex { // an entry in the cache
    /**
     * Keep a cache of open IndexReader's, so that an index does not have to
     * opened for each query.  The cache re-opens an index when it has changed
     * so that additions and deletions are visible ASAP.
     */
    public static java.util.Hashtable indexCache = new java.util.Hashtable(); // name->CachedIndex
    /** DOCUMENT ME! */
    IndexReader reader; // an open reader
    /** DOCUMENT ME! */
    long modified; // reader's modified date
    /** DOCUMENT ME! */
    String name;

    /**
     * Creates a new CachedIndex object.
     *
     * @param name DOCUMENT ME!
     *
     * @throws java.io.IOException DOCUMENT ME!
     */
    CachedIndex(String name) throws java.io.IOException {
        modified = IndexReader.getCurrentVersion(name); // get modified date
        reader = IndexReader.open(name); // open reader
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws java.io.IOException DOCUMENT ME!
     */
    public void close() throws java.io.IOException {
        reader.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public IndexReader getReader() {
        return reader;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws java.io.IOException DOCUMENT ME!
     */
    public void reopen() throws java.io.IOException {
        reader = IndexReader.open(name);
    }
}
