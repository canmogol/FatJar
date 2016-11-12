package sample;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "MY_ENTITY")
public class MyEntity {

    @Id
    @Column(name = "ME_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ME_TABLE_SEQ")
    @SequenceGenerator(name = "ME_TABLE_SEQ", sequenceName = "ME_TABLE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "ME_NAME", length = 200)
    private String name;

    public MyEntity() {
    }

    public MyEntity(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" + id + ", " + name + '}';
    }
}
