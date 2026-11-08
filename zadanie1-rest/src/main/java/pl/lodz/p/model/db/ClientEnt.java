package pl.lodz.p.model.db;


import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator(key = "user_type", value = "CLIENT")
public class ClientEnt extends UserEnt {
}
