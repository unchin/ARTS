## Mybatis中常用的SQL语句

1.BaseResultMap
```
<resultMap id="BaseResultMap" type="com.stylefeng.guns.common.persistence.model.LoginTest">
        <id column="id" property="id" />
        <result column="name" property="name" />
        <result column="password" property="password" />
</resultMap>
```
2.SQL
```
  <sql id="Base_Column_List">
    id, name, password
  </sql>
  ```
3.确切的Select

```
<select id="selectUser" resultMap="BaseResultMap" parameterType="String">
        SELECT <include refid="Base_Column_List" /> FROM login_test
        <where>
        <if test="name != ''">
            name=#{name}
        </if>
        </where>
    </select>
```
4.模糊的Select

```
<select id="selectUsers" resultMap="BaseResultMap" parameterType="String">
        SELECT <include refid="Base_Column_List" /> FROM login_test
        <where>
        <if test="name != ''">
            name like '%#{name}%'
        </if>
        </where>
    </select>
```

5.批量的Select(可用于数据库表的批量导出)

```
<select id="selectBySomeid" parameterType="list" resultMap="BaseResultMap">
        SELECT
        <include refid="Base_Column_List"  />
        FROM login_test WHERE id in
        <foreach collection="Idlist" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </select>
```
6.有选择性的update

```
<update id="updateByPrimaryKeySelective" parameterType="com.mall.pojo.LoginTest">
    update login_test
    <set>
      <if test="name != null">
        username = #{username},
      </if>
      <if test="password != null">
        password = #{password},
      </if>
    </set>
    where id = #{id}
  </update>
```
7.无选择性的uptate

```
<update id="updateByPrimaryKey" parameterType="com.mall.pojo.LoginTest">
    update login_test
    set name = #{username},
      password = #{password},
    where id = #{id}
  </update>
```
8.单个delete
```
<delete id="deleteByid" parameterType="Integer">
        DELETE FROM login_test 
                WHERE id =#{id}
    </delete>
  ```
  
9.批量delete

```
<delete id="deleteByid" parameterType="list">
        DELETE FROM login_test WHERE id in
        <foreach collection="Idlist" item="id" open="(" separator="," close=")">
         #{id}
        </foreach>
    </delete>
```
10.有选择性的单个insert

```
<insert id="insertSelective" parameterType="com.mall.pojo.LoginTest">
    insert into login_test
    <trim prefix="(" suffix=")" suffixOverrides=",">
      <if test="id != null">
        id,
      </if>
      <if test="username != null">
        username,
      </if>
      <if test="password != null">
        password,
      </if>
    </trim>
    <trim prefix="values (" suffix=")" suffixOverrides=",">
      <if test="id != null">
        #{id},
      </if>
      <if test="username != null">
        #{name},
      </if>
      <if test="password != null">
        #{password},
      </if>
    </trim>
  </insert>
```


11.无选择性的单个insert
```
<insert id="insert" parameterType="com.mall.pojo.LoginTest">
    insert into mmall_user (id, username, password)
    values (#{id}, #{username}, #{password})
  </insert>
  ```
12.批量插入

```
 <insert id="batchInsert" parameterType="list">
  insert into mmall_order_item (id, name, password)
    values 
    <foreach collection="List" item="item" index="index" separator=",">
    (
      #{item.id},#{item.name},#{item.password} )
    </foreach>
  </insert>
```
 13.多表更新

```
<update id="updateObjectVersion" parameterType="com.huhu.Dto">
     UPDATE ${dataCode} set OBJECT_VERSION_NUMBER=#{objectVersionNumber}
        <where>
            <if test="codeId != null">
                CODE_ID = #{codeId}
            </if>
            <if test="codeValueId != null">
                AND CODE_VALUE_ID = #{codeValueId}
            </if>
            <if test="productId != null">
                AND PRODUCT_ID = #{productId}
            </if>
            <if test="propertyId != null">
                AND PROPERTY_ID = #{propertyId}
            </if>
            <if test="cmdId != null">
                AND CMD_ID = #{cmdId}
            </if>
            <if test="paramId != null">
                AND PARAM_ID = #{paramId}
            </if>
            <if test="templateId != null">
                AND title = #{templateId}
            </if>
        </where>
    </update>
```
 14.自定义插入某个表：
```
<update id="updateObjectVersion" parameterType="com.xx.xx">
     UPDATE ${dataCode} set OBJECT_VERSION_NUMBER=#{objectVersionNumber}
        <where>
            <if test="codeId != null">
                CODE_ID = #{codeId}
            </if>
            <if test="codeValueId != null">
                AND CODE_VALUE_ID = #{codeValueId}
            </if>
            <if test="productId != null">
                AND PRODUCT_ID = #{productId}
            </if>
            <if test="propertyId != null">
                AND PROPERTY_ID = #{propertyId}
            </if>
            <if test="cmdId != null">
                AND CMD_ID = #{cmdId}
            </if>
            <if test="paramId != null">
                AND PARAM_ID = #{paramId}
            </if>
            <if test="templateId != null">
                AND TEMPLATE_ID = #{templateId}
            </if>
        </where>
    </update>
```