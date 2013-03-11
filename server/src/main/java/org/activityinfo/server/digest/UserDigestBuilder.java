package org.activityinfo.server.digest;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.activityinfo.client.i18n.I18N;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.i18n.LocaleHelper;
import org.activityinfo.server.mail.Message;
import org.activityinfo.server.util.html.HtmlWriter;
import org.apache.commons.lang.StringUtils;

import com.teklabs.gwt.i18n.server.LocaleProxy;

public class UserDigestBuilder {

    private static final Logger LOGGER =
        Logger.getLogger(UserDigestBuilder.class.getName());

    private final GeoDigestRenderer geoDigestRenderer;
    private final ActivityDigestRenderer activityDigestRenderer;

    private User user;
    private Date date;
    private int geoDays;
    private int activityDays;

    public UserDigestBuilder(GeoDigestRenderer geoDigestRenderer,
        ActivityDigestRenderer activityDigestRenderer) {
        this.geoDigestRenderer = geoDigestRenderer;
        this.activityDigestRenderer = activityDigestRenderer;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setGeoDays(int geoDays) {
        this.geoDays = geoDays;
    }

    public void setActivityDays(int activityDays) {
        this.activityDays = activityDays;
    }

    public Message build() throws IOException, MessagingException {
        // set the locale of the messages
        LocaleProxy.setLocale(LocaleHelper.getLocaleObject(user));

        List<String> geoDigests = geoDigestRenderer.render(user,
            calcFrom(date, geoDays));
        String activityDigest = activityDigestRenderer.render(user,
            calcFrom(date, activityDays));

        if (geoDigests.isEmpty()
            && StringUtils.isBlank(activityDigest)) {
            return null;
        }

        // create message, set recipient & bcc
        Message message = new Message();
        message.to(user.getEmail(), user.getName());

        String subject = I18N.MESSAGES.digestSubject(date);
        message.subject(subject);

        // create the html body
        HtmlWriter htmlWriter = new HtmlWriter();

        htmlWriter.startDocument();

        htmlWriter.startDocumentHeader();
        htmlWriter.documentTitle(subject);
        htmlWriter.endDocumentHeader();

        htmlWriter.startDocumentBody();

        String greeting = I18N.MESSAGES.digestGreeting(user.getName());
        htmlWriter.paragraph(greeting);

        String intro = I18N.MESSAGES.digestIntro(date);
        htmlWriter.paragraph(intro);

        for (String geoDigest : geoDigests) {
            htmlWriter.paragraph(geoDigest);
        }

        // htmlWriter.paragraph(activityDigest);

        String signature = I18N.MESSAGES.digestSignature();
        htmlWriter.paragraph(signature);

        htmlWriter.endDocumentBody();
        htmlWriter.endDocument();

        LOGGER.fine("digest:\n" + htmlWriter.toString());

        message.htmlBody(htmlWriter.toString());

        return message;
    }

    private long calcFrom(Date date, int days) {
        return date.getTime() - (days * 24 * 60 * 60 * 1000L);
    }
}