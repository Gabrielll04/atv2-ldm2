create extension if not exists "pgcrypto";

create table if not exists products (
    id uuid primary key default gen_random_uuid(),
    name varchar not null,
    description text not null,
    sku varchar not null unique,
    category varchar not null,
    created_at timestamp default now(),
    updated_at timestamp default now()
);

create table if not exists stock_items (
    id uuid primary key default gen_random_uuid(),
    product_id uuid not null references products(id) on delete cascade,
    quantity integer not null,
    unit_price decimal not null,
    location varchar not null,
    updated_at timestamp default now()
);

create or replace function stock_summary()
returns table (
    product_id uuid,
    product_name varchar,
    total_quantity bigint
)
language sql
as $$
    select
        p.id as product_id,
        p.name as product_name,
        coalesce(sum(s.quantity), 0) as total_quantity
    from products p
    left join stock_items s on s.product_id = p.id
    group by p.id, p.name
    order by p.name;
$$;
