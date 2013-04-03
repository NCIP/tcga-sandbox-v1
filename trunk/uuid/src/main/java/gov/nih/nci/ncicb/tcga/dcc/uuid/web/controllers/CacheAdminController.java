/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers;

import gov.nih.nci.ncicb.tcga.dcc.common.util.AlphanumComparator;
import net.sf.ehcache.Cache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Controller class for the cache admin page
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class CacheAdminController {

    @Resource(name = "beanCache")
    private Cache cache;

    @RequestMapping(value = "/cacheAdmin.htm", method = RequestMethod.POST)
    public String cachePostAdminHandler(ModelMap model,
                                        @RequestParam(value = "remove", required = false) final String key) {

        if (key != null) {
            if ("all".equals(key)) {
                cache.removeAll();
            } else {
                cache.remove(key);
            }
        }
        final List cacheKey = cache.getKeys();
        Collections.sort(cacheKey, getCacheKeySorting());
        model.addAttribute("cacheKeys", cacheKey);
        return "cacheAdmin";
    }

    @RequestMapping(value = "/cacheAdmin.htm", method = RequestMethod.GET)
    public String cacheGetAdminHandler(ModelMap model) {
        final List cacheKey = cache.getKeys();
        Collections.sort(cacheKey, getCacheKeySorting());
        model.addAttribute("cacheKeys", cacheKey);
        return "cacheAdmin";
    }

    private Comparator getCacheKeySorting() {
        final Comparator alphaNum = new AlphanumComparator();
        return new Comparator() {
            public int compare(final Object o1, final Object o2) {
                return alphaNum.compare(o1.toString(), o2.toString());
            }
        };
    }

}//End of Class
