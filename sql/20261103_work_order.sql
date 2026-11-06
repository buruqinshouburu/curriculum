CREATE TABLE IF NOT EXISTS t_csys_work_order (
    id BIGINT NOT NULL AUTO_INCREMENT,
    creator_id BIGINT NOT NULL,
    description VARCHAR(2000) NOT NULL,
    reply VARCHAR(2000) DEFAULT NULL,
    reply_description VARCHAR(2000) DEFAULT NULL,
    reply_time DATETIME DEFAULT NULL,
    reply_version BIGINT NOT NULL DEFAULT 0,
    read_version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_work_order_creator (creator_id, id),
    KEY idx_work_order_unread (creator_id, reply_version, read_version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_csys_work_order_image (
    file_id VARCHAR(255) NOT NULL,
    creator_id BIGINT NOT NULL,
    work_order_id BIGINT DEFAULT NULL,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (file_id),
    KEY idx_work_order_image_order (work_order_id),
    CONSTRAINT fk_work_order_image_order FOREIGN KEY (work_order_id)
        REFERENCES t_csys_work_order (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
