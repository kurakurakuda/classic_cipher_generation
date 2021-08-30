FROM mysql
EXPOSE 3306
ADD ./config/my.cnf /etc/mysql/conf.d/my.cnf
ADD ./initdb/init.sql /docker-entrypoint-initdb.d/init.sql
CMD ["mysqld"]