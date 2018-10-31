package org.eyeseetea.malariacare.data.mappers;


import com.raizlabs.android.dbflow.annotation.NotNull;

import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.sync.importer.IConvertDomainDBVisitor;
import org.eyeseetea.malariacare.domain.entity.Option;

public class OptionConvertFromDomainVisitor implements
        IConvertDomainDBVisitor<Option, OptionDB> {

    @NotNull
    @Override
    public OptionDB visit(@NotNull Option domainModel) {
        OptionDB dbModel = new OptionDB();
        dbModel.setCode(domainModel.getCode());
        dbModel.setName(domainModel.getName());
        dbModel.setOptionAttributeDB(getAttributeFrom(domainModel.getAttribute()));
        return dbModel;
    }

    private OptionAttributeDB getAttributeFrom(Option.Attribute attribute) {
        OptionAttributeDB optionAttributeDB = new OptionAttributeDB();
        optionAttributeDB.setId_option_attribute(attribute.getId());
        optionAttributeDB.setBackground_colour(attribute.getBackgroundColour());
        optionAttributeDB.setHorizontal_alignment(mapHorizontalAttribute(attribute.getHorizontalAlignment()));
        optionAttributeDB.setVertical_alignment(mapVerticalAttribute(attribute.getVerticalAlignment()));
        optionAttributeDB.setText_size(attribute.getTextSize());
        return optionAttributeDB;
    }

    private int mapVerticalAttribute(Option.Attribute.VerticalAlignment verticalAlignment) {
        if(verticalAlignment.equals(Option.Attribute.VerticalAlignment.NONE)){
            return 3;
        }else if(verticalAlignment.equals(Option.Attribute.VerticalAlignment.TOP)){
            return 0;
        }else if(verticalAlignment.equals(Option.Attribute.VerticalAlignment.BOTTOM)){
            return 2;
        }else if(verticalAlignment.equals(Option.Attribute.VerticalAlignment.MIDDLE)){
            return 1;
        }
        return 0;
    }

    private int mapHorizontalAttribute(Option.Attribute.HorizontalAlignment horizontalAlignment) {
        if(horizontalAlignment.equals(Option.Attribute.HorizontalAlignment.NONE)){
            return 3;
        }else if(horizontalAlignment.equals(Option.Attribute.HorizontalAlignment.CENTER)){
            return 1;
        }else if(horizontalAlignment.equals(Option.Attribute.HorizontalAlignment.LEFT)){
            return 0;
        }else if(horizontalAlignment.equals(Option.Attribute.HorizontalAlignment.RIGHT)){
            return 2;
        }
        return 0;
    }

}
