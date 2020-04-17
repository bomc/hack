drop table if exists public.t_ping cascade;

drop SEQUENCE if exists public.hibernate_sequence;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: bomc
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO bomc;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: t_ping; Type: TABLE; Schema: public; Owner: bomc
--

CREATE TABLE public.t_ping (
    c_id bigint NOT NULL,
    c_createdatetime timestamp without time zone NOT NULL,
    c_createuser character varying(255),
    c_modifydatetime timestamp without time zone,
    c_modifyuser character varying(255),
    c_version bigint,
    c_pong character varying(255)
);


ALTER TABLE public.t_ping OWNER TO bomc;

--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: bomc
--

SELECT pg_catalog.setval('public.hibernate_sequence', 1, false);

--
-- Name: t_ping t_ping_pkey; Type: CONSTRAINT; Schema: public; Owner: bomc
--

ALTER TABLE ONLY public.t_ping
    ADD CONSTRAINT t_ping_pkey PRIMARY KEY (c_id);
