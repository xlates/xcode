package foo;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import org.apache.commons.dbcp.PoolingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class MyCustomRepo extends NamedParameterJdbcDaoSupport {

    @Qualifier("poolingDataSource")
    @Autowired
    public void setDs(PoolingDataSource dataSource) {
        setDataSource(dataSource);
    }

    public IndexedContainer createAndInitializeContainer() {

        final IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("myField", String.class, null);

        List<Bar> result = (List<Bar>) getNamedParameterJdbcTemplate().
                query("SELECT * FROM FOO", new HashMap<String, String>(), new RowMapper(){

                    @Override
                    public Object mapRow(ResultSet rs, int i) throws SQLException {

                        //Create bean
                        Bar bar = new Bar();

                        //Parse the value for it
                        bar.id = rs.getLong("ID");
                        bar.myField = (String) rs.getString("myField");

                        //Put the bean values to the Vaadin container as well
                        addValueToContainer(bar, container);

                        return bar;
                    }
                });

        return container;
    }

    private void addValueToContainer(Bar bar, IndexedContainer container) {
        String itemId = bar.id + "-" + new Date().getTime();
        Item item = container.addItem(itemId);

        Property groupCompanyName = item.getItemProperty("myField");
        groupCompanyName.setValue(bar.myField);
    }

    private class Bar {
        public Long id;
        public String myField;
    }

}

