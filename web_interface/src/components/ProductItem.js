import React from 'react';

const ProductItem = ({ product }) => {

    const { title, link, price, image } = product;

    return (
        <div class="item">
            <div class="ui rounded small image">
                <img src={image}/>
            </div>

            <div class="content">
                <a class="header"><a href={link} target="_blank">{ title }</a></a>

                <div class="meta">
                    <span>{ price }</span>
                </div>

                <div class="description">
                    <p>description</p>
                </div>

                <div class="extra">
                    extra
                </div>
            </div>
        </div>
    );
}

export default ProductItem;