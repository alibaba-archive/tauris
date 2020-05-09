package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TLogger;
import com.aliyun.tauris.plugins.filter.mutate.*;

/**
 * Created by ZhangLei on 16/12/11.
 */
public class MutateFilter extends BaseTFilter {


    AddField      addField;
    CopyField     copy;
    TConverter[]  convert;
    String[]      remove;
    CaseFormatter caseformat;
    Reverse       reverse;
    StrTruncate   truncate;
    StrTrim       trim;
    StrSplit      split;
    ArrayJoin     arrayJoin;
    ArrayPush     arrayPush;
    StrReplace    replace;
    SubString     substring;
    DateFormat    dateformat;
    DateParse     dateparse;
    URLDecode     urldecode;
    URLEncode     urlencode;

    public void init() {
        this.logger = TLogger.getLogger(this);
    }

    @Override
    public boolean doFilter(TEvent e) {
        remove(e);
        addField(e);
        copyField(e);
        convert(e);
        caseformat(e);
        reverse(e);
        trim(e);
        replace(e);
        split(e);
        arrayJoin(e);
        arrayPush(e);
        truncate(e);
        dateparse(e);
        dateformat(e);
        urldecode(e);
        urlencode(e);
        substring(e);
        return true;
    }

    private void remove(TEvent e) {
        if (remove == null || remove.length == 0) {
            return;
        }
        for (String r : remove) {
            e.remove(r);
        }
    }

    private void addField(TEvent e) {
        if (addField == null) {
            return;
        }
        addField.mutate(e);
    }

    private void copyField(TEvent e) {
        if (copy == null) {
            return;
        }
        copy.mutate(e);
    }

    private void convert(TEvent e) {
        if (convert == null) {
            return;
        }
        for (TConverter converter : convert) {
            converter.convert(e);
        }
    }

    private void caseformat(TEvent e) {
        if (caseformat == null) {
            return;
        }
        caseformat.mutate(e);
    }

    private void reverse(TEvent e) {
        if (reverse == null) {
            return;
        }
        reverse.mutate(e);
    }

    private void trim(TEvent e) {
        if (trim == null) {
            return;
        }
        trim.mutate(e);
    }

    private void split(TEvent e) {
        if (split == null) {
            return;
        }
        split.mutate(e);
    }

    private void arrayJoin(TEvent e) {
        if (arrayJoin == null) {
            return;
        }
        arrayJoin.mutate(e);
    }

    private void arrayPush(TEvent e) {
        if (arrayPush == null) {
            return;
        }
        arrayPush.mutate(e);
    }

    private void replace(TEvent e) {
        if (replace == null) {
            return;
        }
        replace.mutate(e);
    }

    private void truncate(TEvent e) {
        if (truncate == null) {
            return;
        }
        truncate.mutate(e);
    }

    private void dateparse(TEvent e) {
        if (dateparse == null) {
            return;
        }
        dateparse.mutate(e);
    }

    private void dateformat(TEvent e) {
        if (dateformat == null) {
            return;
        }
        dateformat.mutate(e);
    }

    private void urldecode(TEvent e) {
        if (urldecode == null) {
            return;
        }
        urldecode.mutate(e);
    }

    private void urlencode(TEvent e) {
        if (urlencode == null) {
            return;
        }
        urlencode.mutate(e);
    }

    private void substring(TEvent e) {
        if (substring == null) {
            return;
        }
        substring.mutate(e);
    }
}
