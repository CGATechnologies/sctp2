INSERT INTO filter_templates
(table_name, column_name, label, field_type, field_values, created_at, hint)
values
('households', 'floor_type', 'whose house has this type of floor', 'ListSingle', '', current_timestamp, ''),
('households', 'roof_type', 'whose house has this type of roof', 'ListSingle', '', current_timestamp, ''),
('households', 'wall_type', 'whose house has this type of wall', 'ListSingle', '', current_timestamp, ''),
('households', 'latrine_type', 'that have this type of latrine', 'ListSingle', '', current_timestamp, ''),
('households', 'house_condition', 'whose house is in this condition', 'ListSingle', '', current_timestamp, ''),
('households', 'fuel_source', 'that use this source of fuel', 'ListSingle', '', current_timestamp, ''),
('households', 'water_source', 'that have this type of water source', 'ListSingle', '', current_timestamp, ''),
('households', 'has_chair', 'that have a chair or more?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_radio', 'that have a radio?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_bicycles', 'that have bicycles?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_beds', 'that have beds?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_mattress', 'that have mattresses?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_sleeping_mat', 'that have sleeping mats?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_blankets', 'that have blankets?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_water_can', 'that have a water can?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_kitchen_utencils', 'that have kitchen utencils?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_poultry', 'that have poultry?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_livestock', 'that have livestock?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_ox_cart', 'that have an ox cart?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_hoe', 'that have hoes?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_machete_knife', 'that have panga knives', 'ListSingle', '', current_timestamp, ''),
('households', 'has_mortar', 'that have mortar', 'ListSingle', '', current_timestamp, ''),
('households', 'has_cellphone', 'that have a cellphone?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_no_assets', 'that have no assets?', 'ListSingle', '', current_timestamp, ''),
('households', 'maize_harvest_lasted', 'whose last maize harvest lasted', 'ListSingle', '', current_timestamp, ''),
('households', 'maize_in_granary_will_last', 'whose maize in granary will last', 'ListSingle', '', current_timestamp, ''),
('households', 'meals_eaten_last_week', 'that had eaten this many meals last week', 'ListSingle', '', current_timestamp, ''),
('households', 'has_latrine', 'that have a latrine?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_flush_toilet', 'that have a flush toilet?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_vip_latrine', 'that have a VIP latrine?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_latrine_with_roof', 'that have a roofed latrine?', 'ListSingle', '', current_timestamp, ''),
('households', 'has_other_toilet_type', 'that have other toilet types?', 'ListSingle', '', current_timestamp, ''),
('households', 'receives_monetary_assistance', 'that have receive monetary assistance', 'ListSingle', '', current_timestamp, ''),
('households', 'survives_on_begging', 'that survive on begging', 'ListSingle', '', current_timestamp, ''),
('households', 'survives_on_ganyu', 'that survive on ganyu', 'ListSingle', '', current_timestamp, ''),
('households', 'survives_on_petty_trading', 'that survive on petty trading', 'ListSingle', '', current_timestamp, ''),
('households', 'survives_on_agriculture', 'that survive on agriculture', 'ListSingle', '', current_timestamp, ''),
('households', 'survives_on_other', 'that rely on other', 'ListSingle', '', current_timestamp, ''),
('households', 'assistance_received', 'that receive any form of assistance?', 'ListSingle', '', current_timestamp, ''),
('households', 'wealth_quintile', 'that have wealth quintile of', 'ListSingle', '', current_timestamp, ''),
('households', 'labor_constrained', 'that are labor constrained', 'ListSingle', '', current_timestamp, ''),
('households', 'dependency_ratio', 'that have the following dependency ratio', 'Decimal', '', current_timestamp, '');