# Pagination

This documentation will show you how to create a dedicated paging DTO.
Then consume the API.

Please note that this documentation assumes that you are using hibernate.
Otherwise you will certainly have to adapt the first part.

### Data transfer object

To create a DTO, nothing could be simpler, annotate the header of your class @Entity, then @Immutable, specify the name
of the view: @Table.
And finally, specify the identifier with @Id. But you already know all this ...

```java
import com.replace.replace.api.json.annotation.Group;
import com.replace.replace.api.json.annotation.Json;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
@Entity
@Immutable
@Table( name = "company_pagination" )
public class Company {

    @Json( groups = {
            @Group( name = GroupType.ADMIN )
    } )
    @Id
    private Long   id;
    @Json( groups = {
            @Group( name = GroupType.ADMIN )
    } )
    private String name;
    @Json( groups = {
            @Group( name = GroupType.ADMIN )
    } )
    private Byte   status;
}
```

You will notice that the @Immutable annotation is specific to hibernate.

If you are using another implementation, you need to find an alternative

### Controller

After creating the DTO, create the controller

```java
import com.replace.replace.api.pagination.Pagination;
import com.replace.replace.api.pagination.PaginationBuilder;
import com.replace.replace.api.pagination.exception.NotSupportedKey;
import com.replace.replace.api.pagination.exception.NotSupportedOperator;
import com.replace.replace.api.pagination.exception.NotSupportedValue;
import com.replace.replace.api.request.Request;
import com.replace.replace.module.pagination.dto.Company;
import com.replace.replace.configuration.json.GroupType;


import java.util.Map;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
@RestController( "AdminPaginationController" )
@RequestMapping( path = "/admin/pagination" )
public class PaginationController {

    protected final PaginationBuilder paginationBuilder;
    protected final Request           request;
    protected final SecurityResolver  securityResolver;


    public PaginationController(
            PaginationBuilder paginationBuilder,
            Request request,
            SecurityResolver securityResolver ) {
        this.paginationBuilder = paginationBuilder;
        this.request           = request;
        this.securityResolver  = securityResolver;
    }


    @GetMapping( path = "/company" )
    public ResponseEntity< Map< String, Object > > company() {
        Pagination pagination;

        try {
            pagination = paginationBuilder.getResult( request, Company.class, "company_pagination" );
        } catch ( NotSupportedOperator | NotSupportedKey | NotSupportedValue e ) {
            throw new HttpUnprocessableEntityException( e.getMessage() );
        }

        return ResponseEntity
                .ok( pagination.encode( GroupType.ADMIN ) );
    }
}
```

1: Request <br>
2: Class<DTO> <br>
3: View name

### Consume API

To consume the API, you will have to send your parameters in the url, the parameters are in the following format:

```http request
field[operator]=value
```

#### Basic query string

<table>
    <tr>
        <th>Parameter</th>
        <th>Description</th>
        <th>Default</th>
    </tr>
    <tr>
        <td>sortBy</td>
        <td>fields to sort on</td>
        <td>id</td>
    </tr>
    <tr>
        <td>orderBy</td>
        <td>Sense of sort, ASC | DESC. Case insensitive</td>
        <td>DESC</td>
    </tr>
    <tr>
        <td>per_page</td>
        <td>Number of rows per page</td>
        <td>20</td>
    </tr>
    <tr>
        <td>page</td>
        <td>Requested page</td>
        <td>1</td>
    </tr>
</table>

#### Conditional query string

<table>
    <tr>
        <th>Operator</th>
        <th>SQL value</th>
    </tr>
    <tr>
        <td>eq</td>
        <td>=</td>
    </tr>
    <tr>
        <td>ne</td>
        <td>!=</td>
    </tr>
    <tr>
        <td>sup</td>
        <td>></td>
    </tr>
    <tr>
        <td>inf</td>
        <td><</td>
    </tr>
    <tr>
        <td>supeq</td>
        <td>>=</td>
    </tr>
    <tr>
        <td>infeq</td>
        <td><=</td>
    </tr>
    <tr>
        <td>contains</td>
        <td>LIKE</td>
    </tr>
    <tr>
        <td>necontains</td>
        <td>NOT LIKE</td>
    </tr>
</table>

Exemple:

<table>
    <tr>
        <th>Query string</th>
        <th>SQL result</th>
    </tr>
    <tr>
        <td>id[eq]=10</td>
        <td>WHERE id = 10</td>
    </tr>
    <tr>
        <td>id[ne]=10</td>
        <td>WHERE id != 10</td>
    </tr>
    <tr>
        <td>id[sup]=10</td>
        <td>WHERE id > 10</td>
    </tr>
    <tr>
        <td>id[inf]=10</td>
        <td>WHERE id < 10</td>
    </tr>
    <tr>
        <td>id[supeq]=10</td>
        <td>WHERE id >= 10</td>
    </tr>
    <tr>
        <td>id[infeq]=10</td>
        <td>WHERE id <= 10</td>
    </tr>
    <tr>
        <td>language[contains]=java</td>
        <td>WHERE language LIKE "%java%"</td>
    </tr>
    <tr>
        <td>language[necontains]=python</td>
        <td>WHERE language NOT LIKE "%python%"</td>
    </tr>
    <tr>
        <td>phone[eq]=null</td>
        <td>WHERE phone IS NULL</td>
    </tr>
    <tr>
        <td>phone[ne]=null</td>
        <td>WHERE phone IS NOT NULL</td>
    </tr>
    <tr>
        <td>id[eq]=10 & id[eq]=11</td>
        <td>WHERE (id = 10 OR id = 11)</td>
    </tr>
    <tr>
        <td>id[eq]=10 & id[eq]=11 & language[contains]=java</td>
        <td>WHERE (id = 10 OR id = 11) AND language LIKE "%java%"</td>
    </tr>
</table>


Warning ! The parameters must be encoded, at the risk of receiving a 400 error

### Requirements

- Module git@github.com:romainlavabre/spring-starter-request.git
- Module git@github.com:romainlavabre/spring-starter-json.git

### Versions

##### 3.0.0

- Migrate to jakarta

##### 1.0.1

- ADD Support snack & camel case parameters

##### 1.0.0

INITIAL
