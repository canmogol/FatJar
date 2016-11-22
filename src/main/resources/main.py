from fatjar import Server, Log


def main():
    Server.create().listen(8080, "0.0.0.0") \
        .filter("/*", lambda req, res: Log.info("Wildcard filter called")) \
        .get("/", lambda req, res: (res.setContent("Welcome"), res.write())) \
        .start()


if __name__ == '__main__':
    main()
